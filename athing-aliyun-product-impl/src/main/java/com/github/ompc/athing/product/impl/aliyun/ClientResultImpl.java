package com.github.ompc.athing.product.impl.aliyun;

import com.github.ompc.athing.product.IClientResult;

class ClientResultImpl<T> implements IClientResult<T> {

    private final boolean isSuccess;
    private final String errorCode;
    private final String errorMessage;
    private final T data;
    private final String token;

    ClientResultImpl(boolean isSuccess, String errorCode, String errorMessage, T data, String token) {
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
        this.token = token;
    }

    public static <T> ClientResultImpl<T> failure(String errorCode, String errorMessage) {
        return new ClientResultImpl<>(false, errorCode, errorMessage, null, null);
    }

    public static <T> ClientResultImpl<T> success(String errorCode, String errorMessage, T data, String token) {
        return new ClientResultImpl<>(true, errorCode, errorMessage, data, token);
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public T getData() {
        return data;
    }

}