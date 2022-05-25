package cn.voidnet.scoresystem.judging.worker;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Worker Key Wrong")
public class WorkerKeyWrongException extends RuntimeException {
}

