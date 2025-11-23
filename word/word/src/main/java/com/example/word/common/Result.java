package com.example.word.common;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 私有构造方法
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    // Getters
    public Integer getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}