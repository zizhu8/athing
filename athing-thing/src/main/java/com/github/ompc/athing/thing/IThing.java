package com.github.ompc.athing.thing;

import java.util.Set;

/**
 * 设备
 */
public interface IThing {

    /**
     * 获取产品ID
     *
     * @return 产品ID
     */
    String getProductId();

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    String getThingId();

    /**
     * 列出所有设备属性ID
     *
     * @return 设备属性ID列表
     */
    Set<String> listThingPropertyIds();

    /**
     * 获取设备属性
     *
     * @param propertyId 属性ID
     * @return 设备属性
     */
    IThingProperty getThingProperty(String propertyId);

    /**
     * 列出所有设备服务ID
     *
     * @return 设备服务ID列表
     */
    Set<String> listThingServiceIds();

    /**
     * 获取设备服务
     *
     * @param serviceId 服务ID
     * @return 设备服务
     */
    IThingService getThingService(String serviceId);

}
