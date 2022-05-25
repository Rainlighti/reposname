package cn.voidnet.scoresystem;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.experiment.ExperimentDAO;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.testprocess.TestProcessDAO;
import cn.voidnet.scoresystem.judging.event.*;
import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserDAO;
import cn.voidnet.scoresystem.share.user.UserType;
import cn.voidnet.scoresystem.test.TestPoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ScoreSystemApplicationTests {
    @Resource
    EventDAO eventDAO;
    @Resource
    EventService eventService;
    @Resource
    ExperimentDAO expDAO;
    @Resource
    JudgingDAO judgingDAO;
    @Resource
    TestProcessDAO testProcessDAO;

    @Resource
    UserDAO userDAO;



    @Test
    @Transactional
    void test4(){
        judgingDAO.findJudgingContainTestPoint(9l);
//        testProcessDAO.setTestProcessState("PASS",65l,25l);
//        judgingDAO.getUserAllJudging(2903l);


    }
    @Test
    @Transactional
    void test() {
//        judgingDAO.updateUserScoreSum(0l);
//        judgingDAO.test().forEach(System.out::println);
//        System.out.println(judgingDAO.getUserScoreSum(0l)
//                );

    }
    @Test
    @Transactional()
    void test2(){
        var j = judgingDAO.findByUserIdAndExperimentId(0l,8l).get();
        var e  = j.getEvents();
//        eventService.setState(e,UploadEvent.class, EventState.RUNNING);


    }
    @Test
    void test11(){
//        judgingDAO.deleteById(107l);
        var user  = new User();
        user.setName("王");
        user.setPassword("098f6bcd4621d373cade4e832627b4f6");
        user.setType(UserType.STUDENT);
        user.setStudentId("1");
        userDAO.save(user);


    }

    @Test
    void addExp() {
        var exp = Experiment
                .builder()
                .documentLink("")
                .intro("通过对进程调度算法的模拟，进⼀步理解进程的基本概念，加深对进程运⾏状态和进程调度过程、调度算法的理解。")
                .order(5)
                .title("进程调度")
                .tests(List.of(
                        TestPoint
                                .builder()
                                .name("测试点1")
                                .score(6)
                                .build(),
                        TestPoint
                                .builder()
                                .name("测试点2")
                                .score(8)
                                .build(),
                        TestPoint
                                .builder()
                                .name("测试点3")
                                .score(11)
                                .build()
                ))
                .build();
        expDAO.save(exp);


    }

}
