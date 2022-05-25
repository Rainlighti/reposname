package cn.voidnet.scoresystem.judging.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class UploadEvent extends Event {
    @Override
    public String getName() {
        return "上传代码";
    }

}