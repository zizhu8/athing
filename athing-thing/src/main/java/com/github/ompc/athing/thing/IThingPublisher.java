package com.github.ompc.athing.thing;

import com.github.ompc.athing.thing.impl.ThingImplByProxyTarget;

/**
 * 设备发布器
 * <p>
 * 发布之后设备才会连接平台并提供服务
 * </p>
 */
public interface IThingPublisher {

    /**
     * 发布设备
     *
     * @param thing 设备
     * @return 设备报告器
     * @throws ThingException 发布设备失败
     */
    IThingReporter publish(IThing thing) throws ThingException;

    /**
     * 发布设备
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param proxy     代理对象
     * @return 设备报告器
     * @throws ThingException 发布设备失败
     */
    default IThingReporter publish(String productId, String thingId, Object proxy) throws ThingException {
        return publish(new ThingImplByProxyTarget(productId, thingId, proxy));
    }

}
