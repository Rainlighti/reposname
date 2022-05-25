package cn.voidnet.scoresystem.share.dev;

import cn.voidnet.scoresystem.experiment.ExperimentDAO;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.event.EventDAO;
import cn.voidnet.scoresystem.judging.event.EventService;
import cn.voidnet.scoresystem.judging.event.EventState;
import cn.voidnet.scoresystem.judging.event.UploadEvent;
import javax.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class DevController {
    @Resource
    EventDAO eventDAO;
    @Resource
    EventService eventService;
    @Resource
    ExperimentDAO expDAO;
    @Resource
    JudgingDAO judgingDAO;
    @Transactional()
    @RequestMapping("dev")
    void test2(){
        var j = judgingDAO.findByUserIdAndExperimentId(0l,8l).get();
        eventService.setState(j, UploadEvent.class, EventState.RUNNING);
    }

}

