package cn.voidnet.scoresystem.judging;

import cn.voidnet.scoresystem.experiment.Experiment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class UserJudgingDTO {
    Experiment experiment;
    Judging judging;


    public Optional<Judging> getJudging() {
        return Optional.ofNullable(judging);
    }
}
