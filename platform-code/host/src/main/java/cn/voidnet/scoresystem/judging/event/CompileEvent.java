package cn.voidnet.scoresystem.judging.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class CompileEvent extends Event {
    @Override
    public String getName() {
        return "编译代码";
    }
}