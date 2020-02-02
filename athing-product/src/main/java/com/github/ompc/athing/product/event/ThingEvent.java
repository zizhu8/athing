package com.github.ompc.athing.product.event;

/**
 * 设备事件
 */
public class ThingEvent {

    private final EventType eventType;
    private final String productId;
    private final String thingId;
    private final long occurTimestamp;

    /**
     * 构造设备事件
     *
     * @param eventType      事件类型
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param occurTimestamp 事件发生的时间戳
     */
    protected ThingEvent(EventType eventType, String productId, String thingId, long occurTimestamp) {
        this.eventType = eventType;
        this.productId = productId;
        this.thingId = thingId;
        this.occurTimestamp = occurTimestamp;
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * 获取产品ID
     *
     * @return 产品ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    public String getThingId() {
        return thingId;
    }

    /**
     * 获取事件发生的时间
     *
     * @return 事件发生的时间
     */
    public long getOccurTimestamp() {
        return occurTimestamp;
    }

}
