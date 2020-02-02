package com.github.ompc.athing.thing.impl.aliyun.util;

public class ReflectUtils {

//    /**
//     * 获取设备服务请求类型
//     *
//     * @param service 设备服务
//     * @return 设备服务请求类型
//     */
//    public static Class<?> getThingServiceRequestType(IThingService<?, ?> service) {
//        return Arrays.stream(service.getClass().getGenericInterfaces())
//                .filter(type -> type instanceof ParameterizedType)
//                .map(type -> (ParameterizedType) type)
//                .filter(type -> type.getRawType().equals(IThingService.class))
//                .map(type -> type.getActualTypeArguments()[0])
//                .map(type -> (Class<?>) type)
//                .findAny()
//                .orElse(null);
//    }
//
//    /**
//     * 获取设备属性值类型
//     *
//     * @param property 设备属性
//     * @return 设备属性值类型
//     */
//    public static Class<?> getThingPropertyValueType(IThingProperty<?> property) {
//        return Arrays.stream(property.getClass().getGenericInterfaces())
//                .filter(type -> type instanceof ParameterizedType)
//                .map(type -> (ParameterizedType) type)
//                .filter(type -> type.getRawType().equals(IThingProperty.class))
//                .map(type -> type.getActualTypeArguments()[0])
//                .map(type -> (Class<?>) type)
//                .findAny()
//                .orElse(null);
//    }

}
