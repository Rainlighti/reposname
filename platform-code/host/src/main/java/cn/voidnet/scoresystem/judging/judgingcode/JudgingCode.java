package cn.voidnet.scoresystem.judging.judgingcode;

import cn.voidnet.scoresystem.share.upload.UploadedFile;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class JudgingCode extends UploadedFile {
    String judgingCodeHash;

}
