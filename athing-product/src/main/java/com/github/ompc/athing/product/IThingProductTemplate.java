package com.github.ompc.athing.product;

import com.github.ompc.athing.product.event.IThingEventListener;

/**
 * 产品模版
 */
public interface IThingProductTemplate {

    /**
     * 声明产品拥有属性
     *
     * @param propertyId   属性ID
     * @param propertyType 属性类型
     * @param <V>          属性类型
     * @return this
     */
    <V> IThingProductTemplate hasThingProductProperty(String propertyId, Class<V> propertyType);

    /**
     * 声明产品拥有服务（同步）
     *
     * @param serviceId  服务ID
     * @param returnType 返回类型
     * @param <R>        返回类型
     * @return this
     */
    <R> IThingProductTemplate hasThingProductService(String serviceId, Class<R> returnType);

    /**
     * 声明产品拥有数据
     *
     * @param dataId   数据ID
     * @param dataType 报告数据类型
     * @param <E>      报告数据类型
     * @return this
     */
    <E> IThingProductTemplate hasThingData(String dataId, Class<E> dataType);

    /**
     * 设置设备事件监听器
     *
     * @param listener 设备事件监听器
     * @return this
     */
    IThingProductTemplate setThingEventListener(IThingEventListener listener);

    /**
     * 根据模板定义构造产品
     *
     * @param clientFactory        客户端(平台实现)
     * @param eventConsumerFactory 事件消费者(平台实现)
     * @return 产品
     * @throws ThingProductException 构建产品失败
     */
    IThingProduct makeThingProduct(IFactory<IThingPlatformClient> clientFactory,
                                   IFactory<IThingEventConsumer> eventConsumerFactory) throws ThingProductException;

}
