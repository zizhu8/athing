package com.github.ompc.athing.thing;

/**
 * 设备属性
 */
public interface IThingProperty {

    /**
     * 获取属性ID
     *
     * @return 属性ID
     */
    String getPropertyId();

    /**
     * 是否支持报告
     * <p>
     * 如果不支持报告，在报告属性的时候会主动忽略
     * </p>
     *
     * @return TRUE | FALSE
     */
    boolean isSupportReport();

    /**
     * 是否支持赋值
     * <p>
     * 如果不支持赋值，在设置属性的时候会主动忽略
     * </p>
     *
     * @return TRUE | FALSE
     */
    boolean isSupportPropertySet();

    /**
     * 获取属性值类型
     *
     * @return 属性值类型
     */
    Class<?> getPropertyType();

    /**
     * 设置属性值
     *
     * @param value 属性值
     * @throws ThingException 设置属性值异常
     */
    void set(Object value) throws ThingException;

    /**
     * 上报属性值
     *
     * @throws ThingException 上报属性值失败
     */
    Object report() throws ThingException;

}
