package com.github.ompc.athing.product.event;

import java.util.Map;
import java.util.Set;

import static com.github.ompc.athing.product.event.EventType.THING_REPORT_PROPERTIES;

/**
 * 属性报告事件
 */
public class ThingReportPropertiesEvent extends ThingEvent {

    private final Map<String, PropertyValue> propertyValueMap;

    /**
     * 构造设备事件
     *
     * @param productId        产品ID
     * @param thingId          设备ID
     * @param occurTimestamp   事件发生的时间戳
     * @param propertyValueMap 上报属性值
     */
    public ThingReportPropertiesEvent(String productId, String thingId, long occurTimestamp,
                                      Map<String, PropertyValue> propertyValueMap) {
        super(THING_REPORT_PROPERTIES, productId, thingId, occurTimestamp);
        this.propertyValueMap = propertyValueMap;
    }


    /**
     * 列出事件中的属性ID
     *
     * @return 事件中的属性ID集合
     */
    public Set<String> listPropertyIds() {
        return propertyValueMap.keySet();
    }

    /**
     * 获取属性值
     *
     * @param propertyId 属性ID
     * @return 属性值
     */
    public PropertyValue getPropertyValue(String propertyId) {
        return propertyValueMap.get(propertyId);
    }

    /**
     * 属性值
     */
    public static class PropertyValue {

        private final Object value;
        private final long collectionTimestamp;

        /**
         * 构造属性值
         *
         * @param value               具体值
         * @param collectionTimestamp 采集时间戳
         */
        public PropertyValue(Object value, long collectionTimestamp) {
            this.value = value;
            this.collectionTimestamp = collectionTimestamp;
        }

        /**
         * 获取值
         *
         * @param <T> 值类型
         * @return 值
         */
        @SuppressWarnings("unchecked")
        public <T> T getValue() {
            return (T) value;
        }

        /**
         * 获取属性值采集时间戳
         *
         * @return 属性值采集时间戳
         */
        public long getCollectionTimestamp() {
            return collectionTimestamp;
        }
    }

}
