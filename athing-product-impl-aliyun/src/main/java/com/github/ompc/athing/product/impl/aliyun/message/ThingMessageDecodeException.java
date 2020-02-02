package com.github.ompc.athing.product.impl.aliyun.message;

/**
 * 消息解码异常
 */
public class ThingMessageDecodeException extends Exception {

    public ThingMessageDecodeException(String message) {
        super(message);
    }

    public ThingMessageDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
