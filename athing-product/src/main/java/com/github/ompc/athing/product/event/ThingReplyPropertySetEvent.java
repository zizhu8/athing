package com.github.ompc.athing.product.event;

import java.util.Set;

import static com.github.ompc.athing.product.event.EventType.THING_REPLY_PROPERTIES_SET;

/**
 * 属性设置应答事件
 */
public class ThingReplyPropertySetEvent extends ThingReplyEvent {

    private final Set<String> propertyIds;

    /**
     * 构造设备事件
     *
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param occurTimestamp 事件发生的时间戳
     * @param token          应答令牌
     * @param code           应答编码
     * @param message        应答消息
     * @param propertyIds    设置属性ID集合
     */
    public ThingReplyPropertySetEvent(String productId, String thingId, long occurTimestamp,
                                      String token, int code, String message,
                                      Set<String> propertyIds) {
        super(THING_REPLY_PROPERTIES_SET, productId, thingId, occurTimestamp, token, code, message);
        this.propertyIds = propertyIds;
    }

    /**
     * 获取设置的属性ID集合
     *
     * @return 设置的属性ID集合
     */
    public Set<String> getPropertyIds() {
        return propertyIds;
    }

}
