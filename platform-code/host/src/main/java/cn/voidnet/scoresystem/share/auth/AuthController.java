package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.exception.RequestFormatErrorException;
import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserService;
import cn.voidnet.scoresystem.share.user.exception.LoginFailException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Resource
    private UserService userService;

    @Resource
    private AuthService authService;

    @JsonView(User.OnlyBasicUserInfo.class)
    @PostMapping("/login/by_password")
    public User loginByPassword( @RequestParam String studentId,
                                 @RequestParam String password, HttpServletResponse response) {
        Optional<User> optionalUser = userService.getUserByStudentId(studentId);
        String tokenStr = null;
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (password != null) {
                tokenStr = Optional.of(password)
                        .filter(it -> userService.isPasswordCorrect(studentId, it))
                        .map(it -> authService.login(user))
                        .orElseThrow(() -> {
                            throw new LoginFailException();
                        });
            } else throw new RequestFormatErrorException();
            response.addHeader("token", tokenStr);
            return optionalUser.get();
        } else {
            throw new LoginFailException();
//            optionalUser = Optional.of(userService.register(studentId));
//            tokenStr = authService.login(optionalUser.get());
        }
    }

}
