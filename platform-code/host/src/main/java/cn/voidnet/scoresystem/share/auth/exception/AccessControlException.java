package cn.voidnet.scoresystem.share.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "This user is not allow to access the resource")
public class AccessControlException extends RuntimeException {

}
