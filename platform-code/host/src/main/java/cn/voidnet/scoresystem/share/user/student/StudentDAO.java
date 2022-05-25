package cn.voidnet.scoresystem.share.user.student;

import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDAO extends JpaRepository<User, Long> {
    Page<User> findUserByTypeAndNameContains(UserType type, String name, Pageable page);
    Page<User> findUserByTypeAndStudentIdContains(UserType type, String studentId, Pageable page);
    Page<User> findUserByType(UserType type,Pageable page);
}
