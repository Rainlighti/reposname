package cn.voidnet.scoresystem.share.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "This user have not permission of the operate")
public class AuthenticationFailException extends RuntimeException {
}
