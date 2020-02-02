package com.github.ompc.athing.product.impl.aliyun.message.header;

/**
 * 设备消息:头
 */
public class ThingMessageHeader {

    private final JmsMessageHeader parent;
    private final String productId;
    private final String thingId;
    private final long timestamp;

    public ThingMessageHeader(JmsMessageHeader parent, String productId, String thingId, long timestamp) {
        this.parent = parent;
        this.productId = productId;
        this.thingId = thingId;
        this.timestamp = timestamp;
    }

    public JmsMessageHeader getParent() {
        return parent;
    }

    public String getProductId() {
        return productId;
    }

    public String getThingId() {
        return thingId;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
