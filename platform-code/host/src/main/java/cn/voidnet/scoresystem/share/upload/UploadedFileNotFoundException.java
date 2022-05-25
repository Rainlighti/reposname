package cn.voidnet.scoresystem.share.upload;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Uploaded file not found")
public class UploadedFileNotFoundException extends RuntimeException {
}
