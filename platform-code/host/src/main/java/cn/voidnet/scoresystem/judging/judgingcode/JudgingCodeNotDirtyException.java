package cn.voidnet.scoresystem.judging.judgingcode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code =HttpStatus.NO_CONTENT, reason = "Judging Program Not Dirty")
public class JudgingCodeNotDirtyException extends RuntimeException {
}

