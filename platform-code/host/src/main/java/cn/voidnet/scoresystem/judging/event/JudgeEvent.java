package cn.voidnet.scoresystem.judging.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class JudgeEvent extends Event {
    @Override
    public String getName() {
        return "评测代码";
    }

    public JudgeEvent() {
    }

    public JudgeEvent(Long id, String name, Long time, EventState state, Boolean isFinal) {
        super(id, name, time, state, isFinal);
    }
}