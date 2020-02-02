package com.github.ompc.athing.product.definition;

import java.util.Objects;

/**
 * 产品成员
 */
public class ThingProductMember {

    private final Type type;
    private final String identifier;
    private final Class<?> parameterClass;
    private final Class<?> returnClass;

    /**
     * 构造产品成员
     *
     * @param type           成员类型
     * @param identifier     成员ID
     * @param parameterClass 成员入参类型
     * @param returnClass    成员返回类型
     */
    public ThingProductMember(Type type, String identifier, Class<?> parameterClass, Class<?> returnClass) {
        this.type = type;
        this.identifier = identifier;
        this.parameterClass = parameterClass;
        this.returnClass = returnClass;
    }

    /**
     * 构造产品属性
     *
     * @param propertyId    属性ID
     * @param propertyClass 产品属性类型
     * @return 产品属性成员
     */
    public static ThingProductMember createThingProductProperty(String propertyId, Class<?> propertyClass) {
        return new ThingProductMember(Type.PROPERTY, propertyId, propertyClass, propertyClass);
    }

    /**
     * 构造产品服务
     *
     * @param serviceId   服务ID
     * @param returnClass 服务返回类型
     * @return 产品服务成员
     */
    public static ThingProductMember createThingProductService(String serviceId, Class<?> returnClass) {
        return new ThingProductMember(Type.SERVICE, serviceId, null, returnClass);
    }

    /**
     * 构造产品数据
     *
     * @param dataId    数据ID
     * @param dataClass 数据类型
     * @return 产品报告成员
     */
    public static ThingProductMember createThingProductData(String dataId, Class<?> dataClass) {
        return new ThingProductMember(Type.DATA, dataId, dataClass, dataClass);
    }

    /**
     * 获取成员的唯一索引
     *
     * @return 唯一索引
     */
    public Key getKey() {
        return new Key(getType(), getIdentifier());
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    /**
     * 产品成员类型
     */
    public enum Type {
        PROPERTY,
        SERVICE,
        DATA
    }

    /**
     * 成员唯一索引
     */
    public static class Key {

        private final Type type;
        private final String identifier;

        public Key(Type type, String identifier) {
            this.type = type;
            this.identifier = identifier;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof Key)
                    && ((Key) other).type == type
                    && Objects.equals(((Key) other).identifier, identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, identifier);
        }

    }

}
