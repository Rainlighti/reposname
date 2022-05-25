package cn.voidnet.scoresystem.judging.worker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("worker")
public class WorkerController {
    @Resource
    WorkerService workerService;

    @GetMapping("online")
    public List<Worker> getAllOnlineWorker(){
        return this.workerService.getOnlineWorkers();
    }
}

