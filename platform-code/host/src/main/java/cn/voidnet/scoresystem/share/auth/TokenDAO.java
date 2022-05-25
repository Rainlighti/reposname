package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenDAO extends JpaRepository<Token, Long> {
    Optional<Token> findById(Long id);


    //根据token获取其唯一对应的用户 如果该token不可用 即无法获取结果
    @Query("select  t.user from Token t join t.user where token = :token")
    Optional<User> findUserIfLogin(String token);

    void deleteAllByUser(User user);

    /*@Query("SELECT new cn.voidnet.scoresystem.share.user.User(name,password,studentId,fullDetail) FROM Token JOIN Token.user WHERE token = :token")
    Optional<User> findUserIfLogin(String token);*/
}
