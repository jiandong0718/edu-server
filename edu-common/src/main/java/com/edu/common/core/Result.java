package com.edu.common.core;

/**
 * Result is an alias for R class for backward compatibility
 */
public class Result<T> extends R<T> {

    public Result() {
        super();
    }

    public Result(int code, String msg, T data) {
        super(code, msg, data);
    }

    /**
     * Success
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * Success with data
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * Success with message and data
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * Failure
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "error", null);
    }

    /**
     * Failure with message
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * Failure with code and message
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
