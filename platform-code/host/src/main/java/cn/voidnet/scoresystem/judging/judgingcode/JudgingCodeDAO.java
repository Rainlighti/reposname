package cn.voidnet.scoresystem.judging.judgingcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JudgingCodeDAO extends JpaRepository<JudgingCode, Long> {

    @Query("select jc " +
            "from JudgingCode jc " +
            "inner join Experiment e " +
            "on e.judgingCode=jc " +
            "where e.id=:expId ")
    Optional<JudgingCode> findByExperimentId(Long expId);

    @Query("select case when (count(*) > 0) " +
            "then true else false end " +
            "from JudgingCode jc " +
            "inner join Experiment e " +
            "on e.judgingCode=jc " +
            "where e.id=:expId")
    boolean existsByExperimentId(Long expId);
}
