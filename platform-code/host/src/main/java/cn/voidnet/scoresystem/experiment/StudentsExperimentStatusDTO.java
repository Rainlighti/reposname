package cn.voidnet.scoresystem.experiment;

import cn.voidnet.scoresystem.judging.Judging;
import cn.voidnet.scoresystem.share.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class StudentsExperimentStatusDTO {
    User student;
    Optional<Judging> judging;

}
