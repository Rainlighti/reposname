package cn.voidnet.scoresystem.share.user.student;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code =HttpStatus.PRECONDITION_FAILED, reason = "Table Format Invaild")
public class TableFormatInvalidException extends RuntimeException {
}

