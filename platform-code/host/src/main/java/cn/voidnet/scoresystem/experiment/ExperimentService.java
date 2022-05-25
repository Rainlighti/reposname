package cn.voidnet.scoresystem.experiment;

import cn.voidnet.scoresystem.judging.Judging;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.JudgingService;
import cn.voidnet.scoresystem.judging.judgingcode.JudgingCodeService;
import cn.voidnet.scoresystem.judging.testprocess.TestState;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import cn.voidnet.scoresystem.share.user.UserDAO;
import cn.voidnet.scoresystem.share.user.UserType;
import cn.voidnet.scoresystem.share.util.SetNull;
import cn.voidnet.scoresystem.test.TestPoint;
import cn.voidnet.scoresystem.test.TestPointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentService {
    @Resource
    ExperimentDAO experimentDAO;
    @Resource
    JudgingService judgingService;
    @Resource
    JudgingCodeService judgingCodeService;
    @Resource
    UserDAO userDAO;

    @Resource
    TestPointService testPointService;
    @Resource
    JudgingDAO judgingDAO;

    private StudentsExperimentStatusVO
    mapToStudentExperimentStatusVO(
            StudentsExperimentStatusDTO studentJudging
    ) {
        return StudentsExperimentStatusVO
                .builder()
                .lastSubmitTime(
                        studentJudging
                                .getJudging()
                                .map(Judging::getLastSubmitTime)
                                .orElse(0L)
                )
                .judgingId(
                        studentJudging
                                .getJudging()
                                .map(Judging::getId)
                                .orElse(-1l)
                )
                .studentName(studentJudging.getStudent().getName())
                .studentClass(studentJudging.getStudent().getClazz())
                .studentId(studentJudging.getStudent().getStudentId())
                .score(studentJudging
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
                .build();
    }


    private ExperimentInfoVO mapToExperimentInfo(Experiment exp) {
        return ExperimentInfoVO
                .builder()
                .id(exp.getId())
                .intro(exp.getIntro())
                .documentLink(exp.getDocumentLink())
                .order(exp.getOrder())
                .title(exp.getTitle())
                .hasJudgingProgram(
                        exp.getJudgingCode() != null
                )
                .testPointAmount(exp.getTests().size())
                .fullScore(
                        exp
                                .getTests()
                                .stream()
                                .map(TestPoint::getScore)
                                .reduce((curr, acc) -> curr + acc)
                                .orElse(0)
                )
                .build();

    }

    public List<Experiment> getAllExperimentsBrief() {
        return experimentDAO.findAll()
                .stream()
                .map(SetNull.of(Experiment::setTests))
                .map(SetNull.of(Experiment::setIntro))
                .map(SetNull.of(Experiment::setDocumentLink))
                .map(SetNull.of(Experiment::setJudgingCode))
                .collect(Collectors.toList());
    }

    public List<ExperimentInfoVO> getAllExperiments() {
        return experimentDAO
                .findAll()
                .stream()
                .map(this::mapToExperimentInfo)
                .collect(Collectors.toList())
                ;
    }

    @Transactional
    public void deleteExperiment(Long experimentId) {
        judgingDAO.findAllByExperimentId(experimentId)
                .forEach(judgingService::deleteJudging);
        judgingCodeService.removeJudgingProgramIfPresent(experimentId);
        experimentDAO.deleteById(experimentId);
    }

    @Transactional
    public void editExperiment(Long expId, Experiment experiment) {
        var origin = experimentDAO.findById(expId)
                .orElseThrow(ResourceNotFoundException::new);
        origin.setTitle(experiment.getTitle());
        origin.setOrder(experiment.getOrder());
        origin.setDocumentLink(experiment.getDocumentLink());
        origin.setIntro(experiment.getIntro());
        experimentDAO.save(origin);
    }

    @Transactional
    public void addExperiment(Experiment experiment) {
        var newExperiment =
                Experiment.builder()
                        .documentLink(experiment.getDocumentLink())
                        .intro(experiment.getIntro())
                        .order(experiment.getOrder())
                        .title(experiment.getTitle())
                        .build();
        experimentDAO.save(newExperiment);
    }


    public Page<StudentsExperimentStatusVO>
    getStudentsExperimentStatus(
            Long expId, Pageable pageable
    ) {
//        return null;
        return userDAO
                .findAllByType(UserType.STUDENT, pageable)
                .map(
                        student ->
                                mapToStudentExperimentStatusVO(
                                        StudentsExperimentStatusDTO
                                                .builder()
                                                .student(student)
                                                .judging(
                                                        judgingDAO
                                                                .findByUserIdAndExperimentId(
                                                                        student.getId(),
                                                                        expId
                                                                )
                                                )
                                                .build()
                                )
                );

    }

    public List<TestPoint> getExpTestPoints(Long expId) {
        return experimentDAO.findById(expId)
                .map(Experiment::getTests)
                .orElseThrow(ResourceNotFoundException::new);
    }
    public String getExpTitle(Long expId) {
        return experimentDAO.findById(expId)
                .map(Experiment::getTitle)
                .orElseThrow(ResourceNotFoundException::new);
    }







}
