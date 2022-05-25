package cn.voidnet.scoresystem.judging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JudgingSummaryVO {
    Long judgingId;
    Integer expOrder;
    String expTitle;
    Integer score;

    public JudgingSummaryVO(Integer expOrder, String expTitle) {
        this.expOrder = expOrder;
        this.expTitle = expTitle;
    }

    Integer fullScore;

}
