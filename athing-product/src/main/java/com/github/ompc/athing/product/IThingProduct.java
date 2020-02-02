package com.github.ompc.athing.product;

import java.util.Set;

/**
 * 产品
 */
public interface IThingProduct {

    /**
     * 获取产品ID
     *
     * @return 产品ID
     */
    String getProductId();

    /**
     * 列出所有产品属性ID
     *
     * @return 产品属性ID列表
     */
    Set<String> listThingProductPropertyIds();

    /**
     * 获取产品属性
     *
     * @param propertyId 属性ID
     * @param <V>        属性值类型
     * @return 产品属性
     */
    <V> IThingProductProperty<V> getThingProductProperty(String propertyId);

    /**
     * 列出所有产品服务ID
     *
     * @return 产品服务ID列表
     */
    Set<String> listThingProductServiceIds();

    /**
     * 获取产品服务
     *
     * @param serviceId 服务ID
     * @param <R>       入参类型
     * @param <P>       返回类型
     * @return 产品服务
     */
    <R, P> IThingProductService<P, R> getThingProductService(String serviceId);

}
