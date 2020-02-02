package com.github.ompc.athing.thing.impl;

import com.github.ompc.athing.thing.IThingProperty;
import com.github.ompc.athing.thing.ThingException;

import java.lang.reflect.InvocationTargetException;

/**
 * 设备属性描述封装实现
 */
class ThingPropertyImplByProxyTargetPropertyDescriptor implements IThingProperty {

    private final String productId;
    private final String thingId;
    private final Object target;
    private final PropertyDescriptor propertyDescriptor;

    ThingPropertyImplByProxyTargetPropertyDescriptor(String productId, String thingId, Object target, PropertyDescriptor propertyDescriptor) {
        this.productId = productId;
        this.thingId = thingId;
        this.target = target;
        this.propertyDescriptor = propertyDescriptor;
    }

    @Override
    public String getPropertyId() {
        return propertyDescriptor.getPropertyId();
    }

    @Override
    public boolean isSupportReport() {
        return propertyDescriptor.isSupportGetter();
    }

    @Override
    public boolean isSupportPropertySet() {
        return propertyDescriptor.isSupportSetter();
    }

    @Override
    public Class<?> getPropertyType() {
        return propertyDescriptor.getPropertyType();
    }

    @Override
    public void set(Object value) throws ThingException {

        // 优先Setter
        if (null != propertyDescriptor.getSetter()) {
            try {
                propertyDescriptor.getSetter().invoke(target, value);
            } catch (IllegalAccessException iaCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s access setter=%s occur error!", getPropertyId(), propertyDescriptor.getSetter()),
                        iaCause
                );
            } catch (InvocationTargetException itCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s invoke setter=%s occur error!", getPropertyId(), propertyDescriptor.getSetter()),
                        itCause.getTargetException()
                );
            }
        }

        // 直接访问属性
        else {
            try {
                propertyDescriptor.getField().set(target, value);
            } catch (IllegalAccessException iaCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s access field=%s occur error!", getPropertyId(), propertyDescriptor.getField()),
                        iaCause
                );
            }
        }

    }

    @Override
    public Object report() throws ThingException {

        // 优先Getter
        if (null != propertyDescriptor.getGetter()) {
            try {
                return propertyDescriptor.getGetter().invoke(target);
            } catch (IllegalAccessException iaCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s access getter=%s occur error!", getPropertyId(), propertyDescriptor.getGetter()),
                        iaCause
                );
            } catch (InvocationTargetException itCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s invoke getter=%s occur error!", getPropertyId(), propertyDescriptor.getGetter()),
                        itCause.getTargetException()
                );
            }
        }

        // 直接访问属性
        else {
            try {
                return propertyDescriptor.getField().get(target);
            } catch (IllegalAccessException iaCause) {
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("property=%s access field=%s occur error!", getPropertyId(), propertyDescriptor.getField()),
                        iaCause
                );
            }
        }
    }
}
