package org.gi.gICore.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;
    private final Throwable exception;

    private Result(boolean success, T data, String errorMessage, Throwable exception) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true,data,null,null);
    }

    public static Result<Void> success() {
        return new Result<>(true,null,null,null);
    }

    public static <T> Result<T> failure(String errorMessage) {
        return new Result<>(false,null,errorMessage,null);
    }

    public static <T> Result<T> failure(String errorMessage, Throwable exception) {
        return new Result<>(false, null, errorMessage, exception);
    }

    public static <T> Result<T> failure(Throwable exception) {
        return new Result<>(false, null, exception.getMessage(), exception);
    }

    public static <T> Result<T> of(boolean condition, T data, String errorMessage) {
        return condition ? success(data) : failure(errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }
    public boolean isFailure() {
        return !success;
    }

    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (isFailure()) {
            return failure(errorMessage, exception);
        }
        try {
            return success(mapper.apply(data));
        } catch (Exception e) {
            return failure("Mapping failed", e);
        }
    }

    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (isFailure()) {
            return failure(errorMessage, exception);
        }
        try {
            return mapper.apply(data);
        } catch (Exception e) {
            return failure("FlatMapping failed", e);
        }
    }

    public Result<T> ifSuccess(Consumer<T> action) {
        if (isSuccess() && data != null) {
            action.accept(data);
        }
        return this;
    }

    public Result<T> ifFailure(Consumer<String> action) {
        if (isFailure()) {
            action.accept(errorMessage);
        }
        return this;
    }

    public T orElse(T defaultValue) {
        return isSuccess() ? data : defaultValue;
    }

    public T orElseThrow() {
        if (isSuccess()) {
            return data;
        }
        throw new RuntimeException(errorMessage, exception);
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "Result.Success(" + data + ")";
        } else {
            return "Result.Failure(" + errorMessage + ")";
        }
    }
}
