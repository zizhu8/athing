package com.github.ompc.athing.product.impl.aliyun.message.header;

public class ThingReportDataMessageHeader {

    private final ThingReportMessageHeader parent;
    private final String reportId;
    private final long collectionTimestamp;

    public ThingReportDataMessageHeader(ThingReportMessageHeader parent, String reportId, long collectionTimestamp) {
        this.parent = parent;
        this.reportId = reportId;
        this.collectionTimestamp = collectionTimestamp;
    }

    public ThingReportMessageHeader getParent() {
        return parent;
    }

    public String getReportId() {
        return reportId;
    }

    public long getCollectionTimestamp() {
        return collectionTimestamp;
    }
}
