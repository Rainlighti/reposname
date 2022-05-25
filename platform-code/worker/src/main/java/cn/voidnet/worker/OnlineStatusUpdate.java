package cn.voidnet.worker;

import cn.voidnet.worker.config.WorkerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class OnlineStatusUpdate {

    @Resource
    TestResultService testResultService;
    @Resource
    WorkerInfo workerInfo;


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        testResultService
                .sendWorkerOnlineStatusUpdateResult(true,
                        "online by startup");
        log.info("{} started",workerInfo.getName());

    }
    @EventListener(ContextClosedEvent.class)
    public void beforeStop(){
        testResultService
                .sendWorkerOnlineStatusUpdateResult(false,
                        "offline");
        log.info("{} stopped",workerInfo.getName());
    }
}
