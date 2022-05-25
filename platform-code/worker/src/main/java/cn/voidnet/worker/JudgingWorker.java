package cn.voidnet.worker;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Component
@Slf4j
public class JudgingWorker {
    @Resource
    JudgingRunner judgingRunner;
    @Value("${score.judging-queue-topic}")
    String judgingQueueTopic;

    BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1);
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.HOURS, blockingQueue);
    Semaphore idleWorkers = new Semaphore(1);


    @KafkaListener(topics = "judging-task")
    public void processTaskMessage(TestWorkerTask task) throws InterruptedException {
        log.info("consumed judging task " + task.getJudgingId());
        final Runnable wrapped = () -> {
            try {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                judgingRunner.judge(task.getJudgingId(), task.getExpId());
                log.info("executed judging task " + task.getJudgingId());
            } finally {
                idleWorkers.release();
            }
        };
        threadPoolExecutor.execute(wrapped);
        try {
            idleWorkers.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
