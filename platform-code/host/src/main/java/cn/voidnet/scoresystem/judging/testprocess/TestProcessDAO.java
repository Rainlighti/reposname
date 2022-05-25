package cn.voidnet.scoresystem.judging.testprocess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TestProcessDAO extends JpaRepository<TestProcess, Long> {
    @Query(value = "update test_process tp " +
            "set tp.state=:state " +
            ",tp.reject_reason=:rejectReason " +
            "where tp.id in " +
            "(select jtp.test_processes_id " +
            "from judging_test_processes jtp " +
            "where judging_id=:judgingId" +
            ") " +
            "and tp.test_id = :testPointId",
            nativeQuery = true)
    @Modifying
    @Transactional
    int setTestProcessState(long judgingId, long testPointId, String state, String rejectReason);
}
