package com.github.ompc.athing.product;

/**
 * 产品服务
 *
 * @param <P> 入参类型
 * @param <R> 返回类型
 */
public interface IThingProductService<P, R> {

    /**
     * 获取服务ID
     *
     * @return 服务ID
     */
    String getServiceId();

    /**
     * 服务
     *
     * @param thingId   设备ID
     * @param parameter 入参
     * @return 应答令牌
     * @throws ThingProductException 请求出错
     */
    IThingToken<R> service(String thingId, P parameter) throws ThingProductException;

}
