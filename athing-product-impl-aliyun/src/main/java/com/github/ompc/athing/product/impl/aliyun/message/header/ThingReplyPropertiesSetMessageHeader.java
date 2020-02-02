package com.github.ompc.athing.product.impl.aliyun.message.header;

public class ThingReplyPropertiesSetMessageHeader {

    private final ThingReplyMessageHeader parent;

    public ThingReplyPropertiesSetMessageHeader(ThingReplyMessageHeader parent) {
        this.parent = parent;
    }

    public ThingReplyMessageHeader getParent() {
        return parent;
    }
}
