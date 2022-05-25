package cn.voidnet.scoresystem.share.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Target resource is not exist")
public class ResourceNotFoundException extends RuntimeException {
}
