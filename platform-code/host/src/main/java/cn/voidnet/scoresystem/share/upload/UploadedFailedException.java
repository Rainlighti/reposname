package cn.voidnet.scoresystem.share.upload;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Upload failed")
public class UploadedFailedException extends RuntimeException {
}
