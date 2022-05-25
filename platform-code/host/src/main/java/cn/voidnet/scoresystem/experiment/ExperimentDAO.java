package cn.voidnet.scoresystem.experiment;

import cn.voidnet.scoresystem.test.TestPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperimentDAO extends JpaRepository<Experiment, Long> {
    Optional<Experiment> findByTestsContaining(TestPoint testPoint);
    //   @Query("select new " +
//           "cn.voidnet.scoresystem.experiment.StudentsExperimentStatusDTO( " +
//           "u,j" +
//           ") " +
//           "from User u " +
//           "left join Experiment e " +
//           "on j.experiment.id=:expId " +
//           "left join Judging j " +
//           "on j.user=u " +
//           "and u.type='STUDENT'")
//   Page<StudentsExperimentStatusDTO> getAllStudentExperimentStatus(Long expId, Pageable pageable);
}
