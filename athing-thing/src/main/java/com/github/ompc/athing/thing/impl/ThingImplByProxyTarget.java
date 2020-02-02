package com.github.ompc.athing.thing.impl;

import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.IThingProperty;
import com.github.ompc.athing.thing.IThingService;
import com.github.ompc.athing.thing.annotation.Thing;
import com.github.ompc.athing.thing.annotation.ThingService;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 设备对象封装实现
 * <p>
 * 允许将一个标注{@link Thing}的对象封装为设备对外进行发布
 * </p>
 */
public class ThingImplByProxyTarget implements IThing {

    private final String productId;
    private final String thingId;
    private final Map<String, IThingProperty> thingPropertyMap = new HashMap<>();
    private final Map<String, IThingService> thingServiceMap = new HashMap<>();

    public ThingImplByProxyTarget(String productId, String thingId, Object target) {
        check(target);
        this.productId = productId;
        this.thingId = thingId;

        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(ThingService.class))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .forEach(method -> {
                    final IThingService thingService = new ThingServiceImplByProxyTargetMethod(productId, thingId, target, method);
                    thingServiceMap.put(thingService.getServiceId(), thingService);
                });

        new PropertyDescriptorParser(target.getClass())
                .parse()
                .forEach(propertyDescriptor -> thingPropertyMap.put(
                        propertyDescriptor.getPropertyId(),
                        new ThingPropertyImplByProxyTargetPropertyDescriptor(productId, thingId, target, propertyDescriptor)
                ));

    }

    private Object check(Object target) {
        // 对象类型必须要有@Thing标记
        if (!target.getClass().isAnnotationPresent(Thing.class)) {
            throw new IllegalArgumentException(String.format("%s @Thing is required!", target.getClass()));
        }
        return target;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public String getThingId() {
        return thingId;
    }

    @Override
    public Set<String> listThingPropertyIds() {
        return thingPropertyMap.keySet();
    }

    @Override
    public IThingProperty getThingProperty(String propertyId) {
        return thingPropertyMap.get(propertyId);
    }

    @Override
    public Set<String> listThingServiceIds() {
        return thingServiceMap.keySet();
    }

    @Override
    public IThingService getThingService(String serviceId) {
        return thingServiceMap.get(serviceId);
    }
}
