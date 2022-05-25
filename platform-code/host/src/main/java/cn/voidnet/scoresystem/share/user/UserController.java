package cn.voidnet.scoresystem.share.user;

import cn.voidnet.scoresystem.share.auth.AccessControl;
import cn.voidnet.scoresystem.share.auth.AuthService;
import cn.voidnet.scoresystem.share.auth.CurrUser;
import cn.voidnet.scoresystem.share.user.exception.OriginalPasswordErrorException;
import cn.voidnet.scoresystem.share.user.exception.PermissionDeniedException;
import cn.voidnet.scoresystem.share.user.exception.PropertyNotUnknownException;
import cn.voidnet.scoresystem.share.user.exception.UserAlreayActivatedException;
import cn.voidnet.scoresystem.share.user.invitationCode.InvitationCode;
import cn.voidnet.scoresystem.share.user.invitationCode.InvitationCodeService;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private AuthService authService;

    @Resource
    private InvitationCodeService invitationCodeService;

    @PutMapping("/me/password")
    public void setPassword(@RequestHeader String token, @RequestParam String password) {
        Long userId = authService.findUserIfLogin(token).getId();
        if (userService.isEmptyPassword(userId)) {
            userService.setPassword(userId, password);
        } else throw new PropertyNotUnknownException();
    }

    @PatchMapping("/me/type")
    @AccessControl()
    public void setUserType(@CurrUser User user,
                            @RequestParam UserType userType
    ) {
        if (userType.equals(UserType.STUDENT)
        ) {
            Long userId = user.getId();
//            if (userService.getUserType(userId).equals(UserType.UNKNOWN)) {
            userService.setUserType(userId, userType);
//            } else throw new PropertyNotUnknownException();
        } else throw new PermissionDeniedException();
    }

    @PostMapping("/me/activate_by_code")
    @AccessControl
    public InvitationCode registerByInvitationCode(
            @CurrUser User user,
             @RequestParam String code
    ) {
        if (user.getType() != UserType.UNKNOWN)
            throw new UserAlreayActivatedException();
        var key = invitationCodeService.useKey(code);
        userService.setUserType(user.getId(), key.getUserType());
        return key;
    }


    @PostMapping("/me/name")
    public void updateName(@RequestHeader String token, @RequestParam String name) {
        Long userId = authService.findUserIfLogin(token).getId();
        userService.setName(userId, name);
    }

    @PostMapping("/me/password")
    public void resetPassword(@RequestHeader String token, @RequestParam("old-password") String oldPassword, @RequestParam("new-password") String newPassword) {
        User user = authService.findUserIfLogin(token);
        if (user.getPassword().equals(oldPassword)) {
            userService.setPassword(user.getId(), newPassword);
        } else throw new OriginalPasswordErrorException();
    }

    @GetMapping("/user/{id}")
    public User getUserInfo(@PathVariable Long id) {
        return userService.getUser(id).get();
    }

    @GetMapping("/me/user")
    @AccessControl
    @Transactional
    @JsonView(User.OnlyBasicUserInfo.class)
    public User getMyInfo(@CurrUser User user) {
        return user;
    }

    /*@PostMapping("/user/update")
    public boolean updateInfo(@RequestHeader String token, @RequestHeader String studentId, @RequestBody User user) throws Exception {
        if (tokenService.isTokenAvailable(studentId, token)) {
            return userService.update(studentId, user);
        } else {
            throw new LoginFailException();
        }
    }*/
}
