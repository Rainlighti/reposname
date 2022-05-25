package cn.voidnet.scoresystem.judging.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EventState {
    WAITING("waiting"),
    DONE("done"),
    NOT_START("notStart"),
    RUNNING("running"),
    ERROR("error");

    private String value;

    EventState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventState fromValue(String value) {
        return Arrays
                .stream(values())
                .filter(it -> it.getValue().toLowerCase().equals(value.toLowerCase()))
                .findFirst()
                .get();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
