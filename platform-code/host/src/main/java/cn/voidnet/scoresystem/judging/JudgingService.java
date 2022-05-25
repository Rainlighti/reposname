package cn.voidnet.scoresystem.judging;

import cn.voidnet.scoresystem.judging.testprocess.TestProcess;
import cn.voidnet.scoresystem.judging.testprocess.TestProcessDAO;
import cn.voidnet.scoresystem.judging.testprocess.TestState;
import cn.voidnet.scoresystem.judging.worker.TestWorkerTask;
import cn.voidnet.scoresystem.experiment.ExperimentDAO;
import cn.voidnet.scoresystem.judging.event.EventService;
import cn.voidnet.scoresystem.judging.event.EventState;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import cn.voidnet.scoresystem.share.upload.FileService;
import cn.voidnet.scoresystem.share.upload.UploadedFile;
import cn.voidnet.scoresystem.share.user.UserDAO;
import cn.voidnet.scoresystem.share.util.SetNull;
import cn.voidnet.scoresystem.test.TestPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgingService {
    @Resource
    JudgingDAO judgingDAO;

    @Value("${score.judging-queue-topic}")
    String judgingQueueTopic;
    @Resource
    ExperimentDAO expDAO;

    @Resource
    UserDAO userDAO;

    @Resource
    EventService eventService;
    @Resource
    FileService fileService;
    @Resource
    KafkaTemplate<String, TestWorkerTask> template;

    public Judging getLastJudgingIfNotExistThenCreateNew(Long userId, Long expId) {
        return judgingDAO
                .findByUserIdAndExperimentId(userId, expId)
                .orElseGet(() ->
                        judgingDAO.save(getNewJudging(userId, expId)));
    }

    public Optional<Judging> getLastJudging(Long userId, Long expId) {
        return judgingDAO
                .findByUserIdAndExperimentId(userId, expId);
    }

    public Judging getJudging(Long judgingId) {
        return judgingDAO.findById(judgingId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public void updateUserScoreSumIfExist(Long userId) {
        judgingDAO.updateUserScoreSum(userId);
    }


    @Transactional
    public Judging getNewJudging(Long userId, Long expId) {
        var events = eventService.initEventList();
        var judging = Judging
                .builder()
                .lastSubmitTime(0l)
                .experiment(expDAO
                        .findById(expId)
                        .orElseThrow(ResourceNotFoundException::new)
                )
                .user(
                        userDAO
                                .findById(userId)
                                .orElseThrow(ResourceNotFoundException::new)
                )
                .testProcesses(
                        expDAO
                                .findById(expId)
                                .orElseThrow(ResourceNotFoundException::new)
                                .getTests()
                                .stream()
                                .map(test -> TestProcess
                                        .builder()
                                        .test(test)
                                        .state(TestState.NOT_START)
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .events(events)
                .currEvent(events.get(0))
                .build();
        return judging;
    }

    public boolean isJudgingExist(Long judgingId) {
        return judgingDAO.existsJudgingById(judgingId);
    }

    public void dispatchJudgingTaskToWorker(Judging judging) {
        log.info("produce judging task {} to worker", judging.getId());
        template.send(judgingQueueTopic, TestWorkerTask
                .builder()
                .expId(judging.getExperiment().getId())
                .judgingId(judging.getId())
                .build()
        );

    }

    @Resource
    TestProcessDAO testProcessDAO;


    public int updateTestProcessState(
            TestState state,
            String rejectReason,
            Long judgingId,
            Long testPointId
    ) {
        return testProcessDAO
                .setTestProcessState(judgingId, testPointId, state.toString(), rejectReason);
    }

    @Transactional
    public void setErrorMessage(Long judgingId, String message) {
        var judg = judgingDAO.findById(judgingId)
                .orElseThrow(ResourceNotFoundException::new);
        judg.setErrorMessage(message);
        judgingDAO.save(judg);
    }

    @Transactional
    public void setCompileWarning(Long judgingId, String message) {
        var judg = judgingDAO.findById(judgingId)
                .orElseThrow(ResourceNotFoundException::new);
        judg.setCompileWarning(message);
        judgingDAO.save(judg);
    }

    public void setWorkerName(Long judgingId, String workerName) {
        var judg = judgingDAO.findById(judgingId)
                .orElseThrow(ResourceNotFoundException::new);
        judg.setWorkerName(workerName);
        judgingDAO.save(judg);
    }

    public List<Judging> getJudgingInQueue() {
        return
                List.of(EventState.WAITING, EventState.RUNNING)
                        .stream()
                        .map(judgingDAO::findJudgingByCurrEventState)
                        .reduce(new ArrayList<>(), (a, b) -> {
                            a.addAll(b);
                            return a;
                        })
                        .stream()
                        .map(SetNull.of(Judging::setExperiment))
                        .map(SetNull.of(Judging::setEvents))
                        .map(SetNull.of(Judging::setLastSubmitCode))
                        .collect(Collectors.toList());

    }

    public Judging getJudgingOnlyDynamicPart(Long userId, Long expId) {
        return judgingDAO
                .findByUserIdAndExperimentId(userId, expId)
                .map(SetNull.of(Judging::setLastSubmitCode))
                .map(SetNull.of(Judging::setUser))
                .map(SetNull.of(Judging::setExperiment))
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Judging getJudgingOnlyDynamicPart(Long judgingId) {
        return judgingDAO
                .findById(judgingId)
                .map(SetNull.of(Judging::setLastSubmitCode))
                .map(SetNull.of(Judging::setUser))
                .map(SetNull.of(Judging::setExperiment))
                .orElseThrow(ResourceNotFoundException::new);
    }

    public List<JudgingSummaryVO> getJudgingSummary(Long userId) {
        return judgingDAO.getUserAllExperimentJudging(userId)
                .stream()
                .map(expJudg -> JudgingSummaryVO
                        .builder()
                        .expOrder(expJudg.getExperiment().getOrder())
                        .expTitle(expJudg.getExperiment().getTitle())
                        .judgingId(expJudg
                                .getJudging()
                                .map(Judging::getId)
                                .orElse(-1l)
                        )
                        .score(expJudg
                                .getJudging()
                                .map(judging -> judging
                                        .getTestProcesses()
                                        .stream()
                                        .filter(it -> it.getState().equals(TestState.PASS))
                                        .map(it -> it.getTest().getScore())
                                        .reduce(
                                                0,
                                                Integer::sum
                                        )
                                )
                                .orElse(0)
                        )
                        .fullScore(expJudg
                                .getExperiment()
                                .getTests()
                                .stream()
                                .map(TestPoint::getScore)
                                .reduce(
                                        0,
                                        Integer::sum
                                )
                        )
                        .build()
                )
                .sorted(Comparator.comparingInt(JudgingSummaryVO::getExpOrder))
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteJudging(Judging judging) {
        Optional.ofNullable(judging.getLastSubmitCode())
                .map(UploadedFile::getId)
                .ifPresent(fileService::deleteIfExist);
        judgingDAO.delete(judging);

    }
}












