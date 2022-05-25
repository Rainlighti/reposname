package cn.voidnet.scoresystem.judging;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.judging.event.Event;
import cn.voidnet.scoresystem.judging.event.EventState;
import cn.voidnet.scoresystem.judging.testprocess.TestProcess;
import cn.voidnet.scoresystem.share.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JudgingDAO extends JpaRepository<Judging, Long> {
    Optional<Judging> findByUserIdAndExperimentId(Long userId, Long experimentId);

    @Query("select j from Event e join Judging j " +
            "on e.id=j.currEvent.id " +
            "where type(e)=:eventClass and e.state=:state ")
    List<Judging> findJudging(Class<? extends Event> eventClass, EventState state);

    List<Judging> findAllByUser(User user);

    List<Judging> findAllByExperimentId(Long experimentId);

    List<Judging> findJudgingByCurrEventState(EventState state);

    boolean existsJudgingById(Long id);

    @Query("select j " +
            "from Judging j " +
            "where :testPointId in " +
            "(select tps.test.id from j.testProcesses tps)")
    List<Judging> findJudgingContainTestPoint(Long testPointId);

    Optional<Judging> findTop1ByUserOrderByLastSubmitTimeDesc(User user);

    int countByLastSubmitCodeNotNullAndUser(User user);

    @Query("select distinct " +
            "new cn.voidnet.scoresystem.judging.UserJudgingDTO(" +
            "e,j" +
            ") " +
            "from Experiment e " +
            "left join Judging j " +
            "on (" +
            "j.experiment=e and " +
            "j.user.id=:userId " +
            ") " +
            "group by e"
    )
    List<UserJudgingDTO> getUserAllExperimentJudging(Long userId);

    @Query(value = "update user u " +
            "set u.score_sum = " +
            "(select coalesce(sum(t.score),0) " +
            "from judging j " +
            "inner join judging_test_processes jtp " +
            "on j.id = jtp.judging_id " +
            "inner join test_process tp " +
            "on jtp.test_processes_id = tp.id " +
            "inner join test_point t " +
            "on tp.test_id = t.id " +
            "where j.user_id=:userId " +
            "and tp.state='PASS') " +
            "and u.id=:userId" +
            "", nativeQuery = true
    )
    @Modifying
    int updateUserScoreSum(Long userId);

}











