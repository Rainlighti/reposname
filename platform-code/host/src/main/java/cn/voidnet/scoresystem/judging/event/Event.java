package cn.voidnet.scoresystem.judging.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Inheritance
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,property = "type")
public class Event {
    @Id
    @GeneratedValue
    Long id;

    //TODO:not to persist
    String name;

    @Builder.Default
    Long time = 0L;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    EventState state =EventState.NOT_START;

    @Builder.Default
    Boolean isFinal = false;


}


