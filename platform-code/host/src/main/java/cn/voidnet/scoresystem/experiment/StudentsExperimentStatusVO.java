package cn.voidnet.scoresystem.experiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentsExperimentStatusVO {
    String studentName;
    String studentId;
    String studentClass;
    Long judgingId;
    Integer score;
    Long lastSubmitTime;






}
