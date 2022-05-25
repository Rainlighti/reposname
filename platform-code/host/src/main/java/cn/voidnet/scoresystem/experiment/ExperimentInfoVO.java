package cn.voidnet.scoresystem.experiment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExperimentInfoVO {
    Integer order;
    String title;
    String intro;
    String documentLink;
    Integer fullScore;
    Integer testPointAmount;
    Long id;
    boolean hasJudgingProgram;

}
