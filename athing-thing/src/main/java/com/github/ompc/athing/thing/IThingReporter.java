package com.github.ompc.athing.thing;

/**
 * 设备报告器
 * <p>
 * 设备可以通过报告器向平台发送报告
 * </p>
 */
public interface IThingReporter {

    /**
     * 报告设备属性
     *
     * @param propertyIds 属性ID列表
     * @throws ThingException 报告异常
     */
    void reportThingProperty(String... propertyIds) throws ThingException;

    /**
     * 报告设备数据
     *
     * @param reportId 数据ID
     * @param data     数据
     * @throws ThingException 报告异常
     */
    void reportThingData(String reportId, Object data) throws ThingException;

}