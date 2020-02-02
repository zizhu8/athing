package com.github.ompc.athing.product.event;

/**
 * 事件类型
 */
public enum EventType {

    /**
     * 服务异步返回应答
     */
    THING_REPLY_SERVICE_RETURN,

    /**
     * 属性设置返回应答
     */
    THING_REPLY_PROPERTIES_SET,

    /**
     * 设备报告
     */
    THING_REPORT_DATA,

    /**
     * 设备属性报告
     */
    THING_REPORT_PROPERTIES

}
