package cn.voidnet.scoresystem.share.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "未设置密码")
public class NotSetPasswordException extends RuntimeException {
}
