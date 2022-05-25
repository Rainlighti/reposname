package cn.voidnet.scoresystem.share.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Long> {
    Optional<User> findByStudentId(String studentId);
    Page<User> findAllByType(UserType userType, Pageable pageable);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user SET dtype=:type WHERE id = :userId")
    @Modifying
    void setUserType(Long userId, String type);


    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user SET school_id=:schoolId WHERE id = :userId")
    @Modifying
    void setSchool(Long userId, Long schoolId);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user SET check_state=:checkState WHERE id = :userId")
    @Modifying
    void initCompanyCheckState(Long userId, String checkState);

    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user SET check_state=:checkState WHERE id = :userId")
    @Modifying
    void studentJoinItsSchoolsRootClan(Long userId);

}
