package cn.voidnet.scoresystem.judging.worker;

import cn.voidnet.scoresystem.judging.JudgingService;
import cn.voidnet.scoresystem.judging.testprocess.TestState;
import cn.voidnet.scoresystem.judging.event.*;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Slf4j
public class JudgingResultConsumer {
    @Resource
    ObjectMapper mapper;
    @Resource
    EventService eventService;
    @Resource
    JudgingService judgingService;
    @Resource
    WorkerService workerService;

    @KafkaListener(topics = "judging-result")
    @Transactional
    public void processResultMessage(TestWorkerResult result) throws InterruptedException, JsonProcessingException {
        try {
            var judgingId = result.getJudgingId();
            switch (result.getResultType()) {
                case SYNC_USER_PROGRAM_ERROR:
                    log.error("error in sync user program to {} ,judgingId = {}", result.getWorkerName(), result.getJudgingId());
                    setErrorMessageIgnoreResourceNotFoundExp(
                                    judgingId,
                                    String
                                            .format("Error in sync user program to %s ," +
                                                    "judgingId = %s",result.getWorkerName(),result.getJudgingId())
                                    );
                case SYNC_JUDGING_PROGRAM_ERROR:
                    log.error("error in sync judging program to {} ,judgingId = {}", result.getWorkerName(), result.getJudgingId());
                    setErrorMessageIgnoreResourceNotFoundExp(
                                    judgingId,
                                    String
                                            .format("Error in sync judging program to %s," +
                                                    "judgingId = %s",result.getWorkerName(),result.getJudgingId())
                            );
                case WORKER_ERROR:
                    log.error("worker error ," +
                            "judgingId = {}," +
                            "workerName = {}" +
                            "reason = {}",result.getJudgingId(),result.getWorkerName(),result.getReason());
                    //worker错误可能找不到judgingId
                    setErrorMessageIgnoreResourceNotFoundExp(
                            judgingId,
                            String.format("Error in judging.worker error," +
                                    "judgingId = %s ," +
                                    "workerName = %s ",judgingId, result.getWorkerName())
                    );
                    setStateIgnoreResourceNotFoundExp(judgingId, EventState.ERROR);
                    break;
                case BEGIN_COMPILE:
                    judgingService.setWorkerName(judgingId,result.getWorkerName());
                    eventService.setState(judgingId, CompileEvent.class, EventState.RUNNING);
                    break;
                case END_COMPILE:
                    judgingService.setCompileWarning(judgingId, result.getReason());
                    eventService.setState(judgingId, CompileEvent.class, EventState.DONE);
                    break;
                case COMPILE_ERROR:
                    judgingService.setErrorMessage(judgingId, result.getReason());
                    eventService.setState(judgingId, CompileEvent.class, EventState.ERROR);
                    break;
                case BEGIN_JUDGE:
                    eventService.setState(judgingId,JudgeEvent.class, EventState.RUNNING);
                    break;
                case JUDGE_ERROR:
                    judgingService.setErrorMessage(judgingId, result.getReason());
                    eventService.setState(judgingId, JudgeEvent.class, EventState.ERROR);
                    break;
                case PASS:
                    setTestProcessState(judgingId, result.getTestPointId(), TestState.PASS);
                    break;
                case REJECT:
                    setTestProcessState(judgingId, result.getTestPointId(), TestState.REJECT, result.getReason());
                    break;
                case PENDING:
                    setTestProcessState(judgingId, result.getTestPointId(), TestState.PENDING);
                    break;
                case END_JUDGE:
                    eventService.setState(judgingId, JudgeEvent.class, EventState.DONE);
                    break;
                case JUDGE_PROGRAM_TIMEOUT:
                    log.error("judge program timeout ," +
                            "judgingId = {}," +
                            "workerName = {}" ,
                            result.getJudgingId(),result.getWorkerName());
                    setErrorMessageIgnoreResourceNotFoundExp(
                            judgingId,
                            String.format("Error in judging. worker timeout," +
                                    "judgingId = %s ," +
                                    "workerName = %s ",judgingId, result.getWorkerName())
                    );
                    setStateIgnoreResourceNotFoundExp(judgingId,EventState.ERROR);
                    break;
                case WORKER_ONLINE:
                    log.info("worker {} is online",result.getWorkerName());
                    workerService.setOnline(result.getWorkerName());
                    break;
                case WORKER_OFFLINE:
                    log.info("worker {} is online",result.getWorkerName());
                    workerService.setOffline(result.getWorkerName());
                    break;
            }
            log.info("success in consume judging result from {},judgingId = {},result = {}", result.getWorkerName(),judgingId,result.toString());
        } catch (Exception e) {
            log.error("error in consume judging result {} from {}", result.getJudgingId(), result.getWorkerName());
            log.error(e.getMessage(),e);
        }


    }
    private void setErrorMessageIgnoreResourceNotFoundExp(Long judgingId, String errorMessage)
    {
        try{
            judgingService.setErrorMessage(judgingId, errorMessage);
        }
        catch (ResourceNotFoundException rnfe)
        {
            //
        }
    }
    private void setStateIgnoreResourceNotFoundExp(
            Long judgingId,
            EventState nextState
            )
    {
        try {
            eventService.setState(judgingId,nextState);
        }
        catch (ResourceNotFoundException e)
        {
            //
        }
    }
    private void setTestProcessState(Long judgingId,
                                     Long testPointId,
                                     TestState state
                                     ) {
        setTestProcessState(judgingId, testPointId, state,null);
    }

    private void setTestProcessState(Long judgingId,
                                     Long testPointId,
                                     TestState state,
                                     String rejectReason
                                     ) {
        assertTestProcessUpdateRowCountShouldBe1(
                judgingService
                        .updateTestProcessState(
                                state,
                                rejectReason,
                                judgingId,
                                testPointId
                        ),
                judgingId,
                testPointId
        );
    }

    private void assertTestProcessUpdateRowCountShouldBe1(int rowCount, Long judgingId, Long testPointId) {
        if (rowCount == 1)
            return;
        else{
            if(!judgingService.isJudgingExist(judgingId))
                return;
            if (rowCount == 0)
                log.warn("testPoint {} should not exist in judging {}",
                        testPointId, judgingId
                );
            else if (rowCount > 1)
                log.error("testPoint {} appear more than one in judging {}",
                        testPointId, judgingId
                );
        }

    }

}
