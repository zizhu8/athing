package com.github.ompc.athing.qatest.puppet.domain;

public class Result<T> {

    private final T data;
    private final boolean success;

    public Result(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

}
