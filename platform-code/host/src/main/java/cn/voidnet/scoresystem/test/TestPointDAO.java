package cn.voidnet.scoresystem.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestPointDAO extends JpaRepository<TestPoint, Long> {
    @Query("select SIZE(e.tests) " +
            "from Experiment e " +
            "where e.id=:expId")
    int count(Long expId);


}
