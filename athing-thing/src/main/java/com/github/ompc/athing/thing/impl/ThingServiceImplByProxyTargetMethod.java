package com.github.ompc.athing.thing.impl;

import com.github.ompc.athing.thing.IThingService;
import com.github.ompc.athing.thing.ThingException;
import com.github.ompc.athing.thing.annotation.ThingParameter;
import com.github.ompc.athing.thing.annotation.ThingService;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static com.github.ompc.athing.thing.annotation.ThingService.Mode.ASYNC;
import static com.github.ompc.athing.thing.annotation.ThingService.Mode.SYNC;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 设备服务方法封装实现
 * <p>
 * 从{@link Method}中构建出对应的{@link IThingService}实现
 * </p>
 */
class ThingServiceImplByProxyTargetMethod implements IThingService {

    private final String productId;
    private final String thingId;
    private final Object target;
    private final Method method;

    private final String serviceId;
    private final Map<String, ThingServiceParameter> thingServiceParameterMap;
    private final Map<String, Class<?>> parameterTypeMap;
    private final boolean isSupportSync;
    private final boolean isSupportAsync;

    /**
     * 构造设备服务方法
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param target    目标对象
     * @param method    目标方法
     */
    ThingServiceImplByProxyTargetMethod(String productId, String thingId, Object target, Method method) {
        this.productId = productId;
        this.thingId = thingId;
        this.target = target;
        this.method = method;

        final ThingService thingServiceAnnotation = method.getAnnotation(ThingService.class);
        this.serviceId = isNotBlank(thingServiceAnnotation.value())
                ? thingServiceAnnotation.value()
                : method.getName();
        this.thingServiceParameterMap = getThingServiceParameterMap(method);
        this.parameterTypeMap = getParameterTypeMap(thingServiceParameterMap);
        this.isSupportSync = asList(thingServiceAnnotation.mode()).contains(SYNC);
        this.isSupportAsync = asList(thingServiceAnnotation.mode()).contains(ASYNC);
    }

    // 从参数标注集合中寻找到@ThingParameter
    private ThingParameter getThingParameter(Annotation[] parameterAnnotations) {
        return Stream.of(parameterAnnotations)
                .filter(annotation -> annotation.annotationType() == ThingParameter.class)
                .map(annotation -> (ThingParameter) annotation)
                .findAny()
                .orElse(null);

    }

    // 获取服务参数集合
    private Map<String, ThingServiceParameter> getThingServiceParameterMap(Method method) {
        final Map<String, ThingServiceParameter> thingServiceParameterMap = new HashMap<>();
        final Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
        final Class<?>[] parameterTypeArray = method.getParameterTypes();

        for (int index = 0; index < method.getParameterCount(); index++) {
            final ThingParameter thingParameterAnnotation = getThingParameter(parameterAnnotationsArray[index]);
            if (null == thingParameterAnnotation) {
                continue;
            }
            thingServiceParameterMap.put(
                    thingParameterAnnotation.value(),
                    new ThingServiceParameter(
                            thingParameterAnnotation.value(),
                            index,
                            parameterTypeArray[index]
                    )
            );
        }

        return unmodifiableMap(thingServiceParameterMap);
    }

    // 获取参数类型集合
    private Map<String, Class<?>> getParameterTypeMap(Map<String, ThingServiceParameter> thingServiceParameterMap) {
        return unmodifiableMap(
                thingServiceParameterMap.entrySet()
                        .stream()
                        .collect(toMap(Entry::getKey, e -> e.getValue().type))
        );
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public boolean isSupportSynchronous() {
        return isSupportSync;
    }

    @Override
    public boolean isSupportAsynchronous() {
        return isSupportAsync;
    }

    @Override
    public Map<String, Class<?>> getParameterTypeMap() {
        return parameterTypeMap;
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public Object service(Map<String, Object> parameterMap) throws ThingException {

        // 拼装参数数组
        final Object[] parameterArray = new Object[method.getParameterCount()];
        parameterMap.forEach((name, value) -> {
            final ThingServiceParameter thingServiceParameter = thingServiceParameterMap.get(name);
            if (null != thingServiceParameter) {
                parameterArray[thingServiceParameter.index] = value;
            }
        });

        try {
            return method.invoke(target, parameterArray);
        } catch (IllegalAccessException iaCause) {
            throw new ThingException(
                    productId,
                    thingId,
                    String.format("service=%s access method occur error!", serviceId),
                    iaCause
            );
        } catch (InvocationTargetException itCause) {
            throw new ThingException(
                    productId,
                    thingId,
                    String.format("service=%s invoke occur error!", serviceId),
                    itCause.getTargetException()
            );
        }
    }

    /**
     * 设备服务参数
     */
    private static class ThingServiceParameter {

        final String name;
        final int index;
        final Class<?> type;

        /**
         * 构建设备服务参数
         *
         * @param name  设备参数名称
         * @param index 设备参数在方法参数中的位置
         * @param type  设备参数类型
         */
        private ThingServiceParameter(String name, int index, Class<?> type) {
            this.name = name;
            this.index = index;
            this.type = type;
        }
    }


}
