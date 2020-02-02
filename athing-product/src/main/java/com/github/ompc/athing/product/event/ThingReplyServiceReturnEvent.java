package com.github.ompc.athing.product.event;

import static com.github.ompc.athing.product.event.EventType.THING_REPLY_SERVICE_RETURN;

/**
 * 服务返回应答消息
 */
public class ThingReplyServiceReturnEvent extends ThingReplyEvent {

    private final String serviceId;
    private final Object data;

    /**
     * 构造设备事件
     *
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param occurTimestamp 事件发生的时间戳
     * @param token          应答令牌
     * @param code           应答编码
     * @param message        应答消息
     * @param serviceId      服务ID
     * @param data           返回数据
     */
    public ThingReplyServiceReturnEvent(String productId, String thingId, long occurTimestamp,
                                        String token, int code, String message,
                                        String serviceId, Object data) {
        super(THING_REPLY_SERVICE_RETURN, productId, thingId, occurTimestamp, token, code, message);
        this.serviceId = serviceId;
        this.data = data;
    }

    /**
     * 获取服务ID
     *
     * @return 服务ID
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * 获取服务返回数据
     *
     * @param <T> 数据类型
     * @return 服务返回数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }
}
