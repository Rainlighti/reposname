package cn.voidnet.scoresystem.share.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "User Alreay Activated")
public class UserAlreayActivatedException extends RuntimeException {
}

