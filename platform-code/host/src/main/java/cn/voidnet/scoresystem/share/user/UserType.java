package cn.voidnet.scoresystem.share.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum UserType {
    ADMIN("admin"),
//    TEACHER("teacher"),
    STUDENT("student"),
//    COMPANY("company"),
    UNKNOWN("unknown");


    private String value;

    UserType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UserType fromValue(String value) {
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
