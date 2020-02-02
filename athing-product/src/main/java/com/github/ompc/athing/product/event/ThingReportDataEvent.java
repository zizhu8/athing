package com.github.ompc.athing.product.event;

import static com.github.ompc.athing.product.event.EventType.THING_REPORT_DATA;

/**
 * 设备报告数据事件
 */
public class ThingReportDataEvent extends ThingReportEvent {

    private final String dataId;
    private final long collectionTimestamp;
    private final Object data;

    /**
     * @param productId           产品ID
     * @param thingId             设备ID
     * @param occurTimestamp      事件发生时间戳
     * @param dataId              数据ID
     * @param collectionTimestamp 数据采集时间戳
     * @param data                数据
     */
    public ThingReportDataEvent(String productId, String thingId, long occurTimestamp,
                                String dataId, long collectionTimestamp, Object data) {
        super(THING_REPORT_DATA, productId, thingId, occurTimestamp);
        this.dataId = dataId;
        this.collectionTimestamp = collectionTimestamp;
        this.data = data;
    }

    /**
     * 获取数据ID
     *
     * @return 数据ID
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 获取数据采集时间戳
     *
     * @return 数据采集时间戳
     */
    public long getCollectionTimestamp() {
        return collectionTimestamp;
    }

    /**
     * 获取报告数据
     *
     * @return 报告数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

}
