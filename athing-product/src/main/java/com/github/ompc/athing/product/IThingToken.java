package com.github.ompc.athing.product;

/**
 * 应答令牌
 * <p>
 * 在异步调用的场景中，服务端发起异步调用之后将会获得此令牌。从设备异步回来的应答中也持有一样的令牌。
 * 这样服务端就能知道本次应答回应的是那一次异步调用。
 * </p>
 * <p>
 * 令牌的{@code token}为字符串，在平台规定的时间内保持全局唯一
 * </p>
 *
 * @param <E> 令牌携带数据类型
 */
public interface IThingToken<E> {

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
     * 获取令牌
     *
     * @return 令牌
     */
    String getToken();

    /**
     * 获取令牌携带的数据
     *
     * @return 令牌携带的数据
     */
    E getData();

}
