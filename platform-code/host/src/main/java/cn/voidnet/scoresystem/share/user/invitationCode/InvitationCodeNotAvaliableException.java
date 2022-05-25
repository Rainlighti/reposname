package cn.voidnet.scoresystem.share.user.invitationCode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Register Key Not Avaliable")
public class InvitationCodeNotAvaliableException extends RuntimeException {
}

