package com.github.ompc.athing.thing;

import java.util.Map;

/**
 * 设备服务
 */
public interface IThingService {

    /**
     * 获取服务ID
     *
     * @return 服务ID
     */
    String getServiceId();

    /**
     * 是否支持同步服务
     *
     * @return TRUE | FALSE
     */
    boolean isSupportSynchronous();

    /**
     * 是否支持异步服务
     *
     * @return TRUE | FALSE
     */
    boolean isSupportAsynchronous();

    /**
     * 获取命名参数类型集合
     *
     * @return 命名参数类型集合
     */
    Map<String, Class<?>> getParameterTypeMap();

    /**
     * 获取返回类型
     *
     * @return 返回类型
     */
    Class<?> getReturnType();

    /**
     * 服务调用
     *
     * @param parameterMap 命名参数集合
     * @return 调用结果，如果是异步调用则返回null
     * @throws ThingException 服务调用失败
     */
    Object service(Map<String, Object> parameterMap) throws ThingException;

}
