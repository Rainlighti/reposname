package cn.voidnet.scoresystem.judging.worker;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkerService {
    @Resource
    WorkerDAO workerDAO;

    @Transactional
    public void setOnline(String name) {
        var worker = workerDAO.findByName(name)
                .orElse(
                        Worker.builder()
                                .name(name)
                                .build()
                );
        worker.setOnline(true);
        workerDAO.saveAndFlush(worker);
    }

    @Transactional
    public void setOffline(String name) {
        workerDAO.setOffline(name);
    }

    public boolean isOnline(String name) {
        return workerDAO.isOnline(name);
    }

    public List<Worker> getOnlineWorkers() {
        return workerDAO.findByOnlineEquals(true);
    }
}










