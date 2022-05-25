package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.auth.exception.AuthenticationFailException;
import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.exception.LoginFailException;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class AuthService {
    @Resource
    private TokenDAO tokenDAO;

    public String login(User user) {
        Token token = new Token(user);
        tokenDAO.save(token);
        return token.getToken();
    }

    //根据token获取其唯一对应的用户 如果该token不可用 即throw未登录错误
    @Transactional
    public User findUserIfLogin(String token) {
        var user = tokenDAO.findUserIfLogin(token).orElseThrow(AuthenticationFailException::new);
        Hibernate.initialize(user);
        return user;
    }
    @Transactional
    public void invaildTokens(User user)
    {
        tokenDAO.deleteAllByUser(user);
    }
}
