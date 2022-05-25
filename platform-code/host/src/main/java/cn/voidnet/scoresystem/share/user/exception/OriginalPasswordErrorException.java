package cn.voidnet.scoresystem.share.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PAYMENT_REQUIRED, reason = "Original password incorrect")
public class OriginalPasswordErrorException extends RuntimeException {
}
