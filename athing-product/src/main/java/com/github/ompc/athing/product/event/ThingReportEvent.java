package com.github.ompc.athing.product.event;

abstract public class ThingReportEvent extends ThingEvent {

    /**
     * 构造设备事件
     *
     * @param eventType      事件类型
     * @param productId      产品ID
     * @param thingId        设备ID
     * @param occurTimestamp 事件发生的时间戳
     */
    protected ThingReportEvent(EventType eventType, String productId, String thingId, long occurTimestamp) {
        super(eventType, productId, thingId, occurTimestamp);
    }

}
