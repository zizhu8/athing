package com.github.ompc.athing.product;


/**
 * 设备平台客户端
 * <p>
 * 由各个平台商实现
 * </p>
 */
public interface IThingPlatformClient {

    /**
     * 设备属性赋值
     *
     * @param productId  产品ID
     * @param thingId    设备ID
     * @param propertyId 属性ID
     * @param value      属性值
     * @return 赋值结果
     * @throws ThingProductException 平台操作失败
     */
    IClientResult<Void> assignThingPropertyValue(String productId, String thingId, String propertyId, Object value) throws ThingProductException;

    /**
     * 设备服务(同步)调用
     *
     * @param productId  产品ID
     * @param thingId    设备ID
     * @param serviceId  服务ID
     * @param returnType 返回值类型
     * @param parameter  服务参数
     * @param <R>        返回值类型
     * @return 服务调用结果（携带返回值）
     * @throws ThingProductException 平台操作失败
     */
    <R> IClientResult<R> syncInvokeThingService(String productId, String thingId, String serviceId, Class<R> returnType, Object parameter) throws ThingProductException;

    /**
     * 设备服务(异步)调用
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param serviceId 服务ID
     * @param parameter 服务参数
     * @return 服务调用结果
     * @throws ThingProductException 平台操作失败
     */
    IClientResult<Void> asyncInvokeThingService(String productId, String thingId, String serviceId, Object parameter) throws ThingProductException;


}
