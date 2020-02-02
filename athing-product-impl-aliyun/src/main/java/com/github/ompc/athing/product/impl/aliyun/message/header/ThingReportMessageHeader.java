package com.github.ompc.athing.product.impl.aliyun.message.header;

public class ThingReportMessageHeader {

    private final ThingMessageHeader parent;

    public ThingReportMessageHeader(ThingMessageHeader parent) {
        this.parent = parent;
    }

    public ThingMessageHeader getParent() {
        return parent;
    }
}
