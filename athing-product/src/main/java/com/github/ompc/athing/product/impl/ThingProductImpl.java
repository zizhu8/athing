package com.github.ompc.athing.product.impl;

import com.github.ompc.athing.product.IThingProduct;
import com.github.ompc.athing.product.IThingProductProperty;
import com.github.ompc.athing.product.IThingProductService;

import java.util.Map;
import java.util.Set;

/**
 * 产品内部实现
 */
class ThingProductImpl implements IThingProduct {

    private final String productId;
    private final Map<String, IThingProductProperty<?>> thingProductPropertyMap;
    private final Map<String, IThingProductService<?, ?>> thingProductServiceMap;

    ThingProductImpl(final String productId,
                     final Map<String, IThingProductProperty<?>> thingProductPropertyMap,
                     final Map<String, IThingProductService<?, ?>> thingProductServiceMap) {
        this.productId = productId;
        this.thingProductPropertyMap = thingProductPropertyMap;
        this.thingProductServiceMap = thingProductServiceMap;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public Set<String> listThingProductPropertyIds() {
        return thingProductPropertyMap.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> IThingProductProperty<V> getThingProductProperty(String propertyId) {
        return (IThingProductProperty<V>) thingProductPropertyMap.get(propertyId);
    }

    @Override
    public Set<String> listThingProductServiceIds() {
        return thingProductServiceMap.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R, P> IThingProductService<P, R> getThingProductService(String serviceId) {
        return (IThingProductService<P, R>) thingProductServiceMap.get(serviceId);
    }

}
