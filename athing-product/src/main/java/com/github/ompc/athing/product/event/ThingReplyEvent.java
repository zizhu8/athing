package com.github.ompc.athing.product.event;

/**
 * 设备应答事件
 */
abstract public class ThingReplyEvent extends ThingEvent {

    private final String token;
    private final int code;
    private final String message;

    /**
     * 构造设备事件
     *
     * @param eventType      事件类型
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param occurTimestamp 事件发生的时间戳
     * @param token          应答令牌
     * @param code           应答编码
     * @param message        应答消息
     */
    protected ThingReplyEvent(EventType eventType, String productId, String thingId, long occurTimestamp,
                              String token, int code, String message) {
        super(eventType, productId, thingId, occurTimestamp);
        this.token = token;
        this.code = code;
        this.message = message;
    }

    /**
     * 获取应答令牌
     *
     * @return 应答令牌
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取应答编码
     *
     * @return 应答编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取应答消息
     *
     * @return 应答消息
     */
    public String getMessage() {
        return message;
    }
}
