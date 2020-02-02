package com.github.ompc.athing.product.impl.aliyun.message.header;

public class ThingReplyServiceReturnMessageHeader {

    private final ThingReplyMessageHeader parent;
    private final String serviceId;

    public ThingReplyServiceReturnMessageHeader(ThingReplyMessageHeader parent, String serviceId) {
        this.parent = parent;
        this.serviceId = serviceId;
    }

    public ThingReplyMessageHeader getParent() {
        return parent;
    }

    public String getServiceId() {
        return serviceId;
    }
}
