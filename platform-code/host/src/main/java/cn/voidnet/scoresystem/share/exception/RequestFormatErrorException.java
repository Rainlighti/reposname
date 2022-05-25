package cn.voidnet.scoresystem.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "need interview entity when stage change to interviewing")
public class RequestFormatErrorException extends RuntimeException {
}
