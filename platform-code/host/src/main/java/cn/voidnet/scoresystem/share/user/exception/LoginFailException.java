package cn.voidnet.scoresystem.share.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "This studentId is not registered or password incorrect")
public class LoginFailException extends RuntimeException {
}
