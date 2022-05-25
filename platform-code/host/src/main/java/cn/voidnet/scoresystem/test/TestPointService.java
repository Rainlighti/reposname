package cn.voidnet.scoresystem.test;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.experiment.ExperimentDAO;
import cn.voidnet.scoresystem.judging.Judging;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.JudgingService;
import cn.voidnet.scoresystem.judging.testprocess.TestProcess;
import cn.voidnet.scoresystem.judging.testprocess.TestProcessDAO;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.function.Predicate;

@Service
public class TestPointService {
    @Resource
    TestPointDAO testPointDAO;
    @Resource
    ExperimentDAO experimentDAO;

    @Resource
    JudgingDAO judgingDAO;
    @Resource
    TestProcessDAO testProcessDAO;
    @Resource
    JudgingService judgingService;

    public int getTestPointAmount(Long expId) {
        return testPointDAO.count(expId);
    }


    public void addTestPoint(
            Long expId,
            TestPoint testPoint
    ) {
        testPoint.setId(null);
//        testPoint = testPointDAO.save(testPoint);
        var exp = this.experimentDAO
                .findById(expId)
                .orElseThrow(ResourceNotFoundException::new);
        exp.getTests()
                .add(testPoint);
        experimentDAO.save(exp);
    }

    @Transactional
    public void deleteTestPoint(Long testPointId) {
        var exp = testPointDAO
                .findById(testPointId)
                .map(experimentDAO::findByTestsContaining)
                .orElseThrow(ResourceNotFoundException::new)
                .orElseThrow(ResourceNotFoundException::new)
                ;
        exp.getTests()
                .removeIf(it->it.getId().equals(testPointId));
        experimentDAO.save(exp);
        var judgings = judgingDAO
                .findJudgingContainTestPoint(testPointId);
        Predicate<TestProcess> isTestIdEqual = tp -> tp
                .getTest()
                .getId()
                .equals(testPointId);
        judgings.forEach(judging -> {
                    judging.getTestProcesses()
                            .stream()
                            .filter(isTestIdEqual)
                            .forEach(
                                    tp -> testProcessDAO
                                            .delete(tp)
                            );
                    judging.getTestProcesses()
                            .removeIf(isTestIdEqual
                            );
                }
        );
        judgings = judgingDAO.saveAll(judgings);
        judgings.
                stream()
                .map(Judging::getUser)
                .forEach(user -> judgingService
                        .updateUserScoreSumIfExist(user.getId())
                );
        testPointDAO.deleteById(testPointId);
    }

    @Transactional
    public void editTestPoint(Long testPointId, TestPoint testPoint) {
        var origin = testPointDAO.findById(testPointId)
                .orElseThrow(ResourceNotFoundException::new);
        origin.setName(testPoint.getName());
        origin.setScore(testPoint.getScore());
        testPointDAO.save(origin);
    }


}














