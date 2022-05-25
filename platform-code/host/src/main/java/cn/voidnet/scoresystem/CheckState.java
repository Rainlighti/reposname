package cn.voidnet.scoresystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum CheckState {
    UNCHECK("uncheck"), PASS("pass"), DENY("deny");
    private String value;

    CheckState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CheckState fromValue(String value) {
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
