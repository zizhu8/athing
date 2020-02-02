package com.github.ompc.athing.thing.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 属性描述信息
 */
class PropertyDescriptor {

    private final String propertyId;
    private Field field;
    private Method setter;
    private Method getter;

    PropertyDescriptor(String propertyId) {
        this.propertyId = propertyId;
    }

    public boolean isSupportGetter() {
        return null != field || null != getter;
    }

    public boolean isSupportSetter() {
        return null != field || null != setter;
    }

    public Class<?> getPropertyType() {
        if (null != field) {
            return field.getType();
        } else if (null != getter) {
            return getter.getReturnType();
        } else if (null != setter) {
            return setter.getParameterTypes()[0];
        } else {
            return null;
        }
    }


    public String getPropertyId() {
        return propertyId;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }
}
