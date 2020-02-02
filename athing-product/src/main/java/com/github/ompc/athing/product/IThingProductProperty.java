package com.github.ompc.athing.product;

/**
 * 产品属性
 *
 * @param <V> 属性值类型
 */
public interface IThingProductProperty<V> {

    /**
     * 获取属性ID
     *
     * @return 属性ID
     */
    String getPropertyId();

    /**
     * 设置属性值
     *
     * @param thingId 设备ID
     * @param value   属性值
     * @return 应答令牌
     * @throws ThingProductException 请求出错
     */
    IThingToken<Void> set(String thingId, V value) throws ThingProductException;

}
