package com.example.persistencedemo.enums;

/**
 * @author: 苏敏
 * @date: 2020/6/27 20:20
 */
public enum PersistenceEnum {
    // 值对象
    VALUE(0, "值"),

    // 对象
    OBJECT(1, "对象"),

    // 集合对象
    ARRAY(2, "集合");


    private int code;
    private String message;

    PersistenceEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
