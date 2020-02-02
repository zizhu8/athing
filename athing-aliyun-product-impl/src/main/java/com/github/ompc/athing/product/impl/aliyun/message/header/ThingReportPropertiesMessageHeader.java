package com.github.ompc.athing.product.impl.aliyun.message.header;

public class ThingReportPropertiesMessageHeader {

    private final ThingReportMessageHeader parent;

    public ThingReportPropertiesMessageHeader(ThingReportMessageHeader parent) {
        this.parent = parent;
    }

    public ThingReportMessageHeader getParent() {
        return parent;
    }

}
