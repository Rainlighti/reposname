package cn.voidnet.scoresystem.share.user.student;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentInfoVO {
    Long id;
    String name;
    String studentId;
    String clazz;
    Integer scoreSum;
    Integer triedExpCount;
    Long lastSubmitTime;
}
