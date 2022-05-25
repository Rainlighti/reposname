package cn.voidnet.scoresystem.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Enum Deseriallization Failed")
public class EnumDeseriallizationException extends RuntimeException {
}

