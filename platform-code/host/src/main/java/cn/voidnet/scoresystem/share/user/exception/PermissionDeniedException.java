package cn.voidnet.scoresystem.share.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Operation unauthorized")
public class PermissionDeniedException extends RuntimeException {
}
