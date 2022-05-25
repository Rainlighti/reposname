package cn.voidnet.worker;

import cn.voidnet.worker.config.WorkerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class JudgingRunner {
    @Resource
    SyncCodeService syncCodeService;
    @Value("${score.worker.python-path}")
    String pythonPath;
    @Resource
    WorkerInfo workerInfo;
    @Resource
    TestResultService testResultService;

    public void judge(Long judgingId, Long expId) {
        try {
            var workDirPath = syncFiles(judgingId, expId);
            if (workDirPath == null) {
                log.error("sync file error,so suspend judging" +
                        ",judgingId = {}", judgingId);
                return;
            }


            executeJudgingWork(judgingId, workDirPath);


            //new process

        } catch (Exception e) {
            log.error("worker unhandled exception: {}", e.getMessage());
            log.error(e.getMessage(), e);
            testResultService.sendWorkerErrorResult(judgingId, e.getMessage());
        } finally {
            syncCodeService.backupUserCode(judgingId);
//            syncCodeService.cleanUserCodeFolder(judgingId);

        }
    }

    private void executeJudgingWork(Long judgingId, Path workDirPath) {
        ProcessBuilder pb = new ProcessBuilder(
                pythonPath,
                "./judge.py",
                workerInfo.toString()
        )
                .directory(workDirPath.toFile())
                .redirectErrorStream(true);
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            log.error("failed to create process");
            log.error(e.getMessage(), e);
            testResultService.sendWorkerErrorResult(judgingId,
                    "failed to create process :" + e.getMessage());
            return;
        }
//        try {
//            process.waitFor();
//        } catch (InterruptedException e) {
//            log.warn("judging work interrupted");
//            testResultService.sendWorkerErrorResult(judgingId,"judging work interrupted");
//        }
        AtomicBoolean isDone = new AtomicBoolean(false);

//        new Thread(() ->
//        {
//            try {
//                process.waitFor(5, TimeUnit.MINUTES);
//                if(!isDone.get())
//                {
//                    process.destroy();
//                    testResultService.sendJudgeProgramTimeoutResult(judgingId);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        ).start();
        ObjectMapper mapper = new ObjectMapper();
        new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .forEachOrdered(line -> {
                    TestWorkerResult result;
                    try {
                        result = mapper.readValue(line, TestWorkerResult.class);
                    } catch (JsonProcessingException e) {
                        log.error("judging code result format error:{}",line);
                        log.error(e.getMessage(), e);
                        testResultService.sendWorkerErrorResult(judgingId, "judging code format error");
                        isDone.set(true);
                        return;
                    }
                    result.setJudgingId(judgingId);
                    result.setWorkerName(workerInfo.toString());
                    testResultService.sendResult(result);
                    if(isFinal(result.getResultType()))
                        isDone.set(true);
                });
    }

    private boolean isFinal(ResultType resultType) {
        switch (resultType) {
            case COMPILE_ERROR:
            case JUDGE_ERROR:
            case END_JUDGE:
            case SYNC_JUDGING_PROGRAM_ERROR:
            case SYNC_USER_PROGRAM_ERROR:
            case WORKER_ERROR:
            case JUDGE_PROGRAM_TIMEOUT:
                return true;
        }
        return false;
    }

    private Path syncFiles(Long judgingId, Long expID) {
        try {
            syncCodeService.syncJudgingCodeToLocal(expID);
        } catch (Exception e) {
            log.error("error in syncJudgingCodeToLocal()" +
                    " expId = {} " +
                    " judgingId = {}", expID, judgingId);
            log.error(e.getMessage(), e);
            testResultService.sendWorkerErrorResult(judgingId,
                    ResultType.SYNC_JUDGING_PROGRAM_ERROR,
                    e.getMessage()
            );
            return null;
        }
        try {
            syncCodeService.syncUserCodeToLocal(judgingId);
        } catch (Exception e) {
            log.error("error in syncUserCodeToLocal()" +
                    " expId = {} " +
                    " judgingId = {}", expID, judgingId);
            log.error(e.getMessage(), e);
            testResultService.sendWorkerErrorResult(judgingId,
                    ResultType.SYNC_USER_PROGRAM_ERROR,
                    e.getMessage()
            );
            return null;
        }
        try {
            return syncCodeService.moveAllJudgingDependencyToJudgingWorkDir(judgingId, expID);
        } catch (Exception e) {
            log.error("error in moveAllJudgingDependencyToJudgingWorkDir()" +
                    " expId = {} " +
                    " judgingId = {}", expID, judgingId);
            log.error(e.getMessage(), e);
            testResultService.sendWorkerErrorResult(judgingId,
                    ResultType.WORKER_ERROR,
                    e.getMessage()
            );
            return null;
        }

    }

}
