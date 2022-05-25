package cn.voidnet.worker;

import cn.voidnet.worker.config.WorkerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestResultService {
    @Resource
    KafkaTemplate<String, Object> kafka;
    @Value("${score.judging-result-topic}")
    String judgingResultTopic;
    @Resource
    WorkerInfo workerInfo;

    public void sendResult(TestWorkerResult tws) {
        kafka.send(judgingResultTopic, tws);
    }

    public void sendWorkerErrorResult(
            Long judgingId,
            ResultType resultType,
            String reason
    ) {
        sendResult(TestWorkerResult
                .builder()
                .reason(reason)
                .judgingId(judgingId)
                .resultType(resultType)
                .workerName(workerInfo.toString())
                .build());
    }
    public void sendWorkerOnlineStatusUpdateResult(
            boolean newOnlineStatus,
            String message
    ) {
        sendResult(TestWorkerResult
                .builder()
                .reason(message)
                .resultType(newOnlineStatus
                        ?ResultType.WORKER_ONLINE
                        :ResultType.WORKER_OFFLINE
                )
                .workerName(workerInfo.toString())
                .build());
    }
    public void sendWorkerErrorResult(
            Long judgingId,
            String reason
    ) {
        sendResult(TestWorkerResult
                .builder()
                .reason(reason)
                .judgingId(judgingId)
                .resultType(ResultType.WORKER_ERROR)
                .workerName(workerInfo.toString())
                .build());
    }
    public void sendJudgeProgramTimeoutResult(
            Long judgingId
    ) {
        sendResult(TestWorkerResult
                .builder()
                .reason("judging program execute time out")
                .judgingId(judgingId)
                .resultType(ResultType.JUDGE_PROGRAM_TIMEOUT)
                .workerName(workerInfo.toString())
                .build());
    }


}
