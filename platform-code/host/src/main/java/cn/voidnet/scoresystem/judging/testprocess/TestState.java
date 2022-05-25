package cn.voidnet.scoresystem.judging.testprocess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TestState {
    NOT_START("notStart"),
    PENDING("pending"),
    PASS("pass"),
    REJECT("reject");

    private String value;

    TestState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TestState fromValue(String value) {
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
