package com.github.ompc.athing.product.impl;

import com.github.ompc.athing.product.*;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.event.IThingEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.github.ompc.athing.product.definition.ThingProductMember.*;

public class ThingProductTemplate implements IThingProductTemplate {

    private final ThingProductDefinition definition;
    private IThingEventListener listener;

    public ThingProductTemplate(String productId) {
        this.definition = new ThingProductDefinition(productId);
    }

    @Override
    public <V> IThingProductTemplate hasThingProductProperty(String propertyId, Class<V> propertyType) {
        definition.defineThingProductMember(createThingProductProperty(propertyId, propertyType));
        return this;
    }

    @Override
    public <R> IThingProductTemplate hasThingProductService(String serviceId, Class<R> returnType) {
        definition.defineThingProductMember(createThingProductService(serviceId, returnType));
        return this;
    }

    @Override
    public <E> IThingProductTemplate hasThingData(String dataId, Class<E> dataType) {
        definition.defineThingProductMember(createThingProductData(dataId, dataType));
        return this;
    }

    @Override
    public IThingProductTemplate setThingEventListener(IThingEventListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public IThingProduct makeThingProduct(IFactory<IThingPlatformClient> clientFactory, IFactory<IThingEventConsumer> eventConsumerFactory) throws ThingProductException {

        final String productId = definition.getProductId();
        final IThingPlatformClient client = clientFactory.make(definition);

        if (null != listener) {
            final IThingEventConsumer consumer = eventConsumerFactory.make(definition);
            consumer.setThingEventListener(listener);
        }

        final Map<String, IThingProductProperty<?>> thingProductPropertyMap = new HashMap<>();
        final Map<String, IThingProductService<?, ?>> thingProductServiceMap = new HashMap<>();
        definition.forEach(member -> {

            switch (member.getType()) {
                case PROPERTY: {
                    thingProductPropertyMap.put(
                            member.getIdentifier(),
                            new ThingProductPropertyImpl<>(
                                    client,
                                    productId,
                                    member.getIdentifier()
                            )
                    );
                    break;
                }
                case SERVICE: {
                    thingProductServiceMap.put(
                            member.getIdentifier(),
                            new ThingProductServiceImpl<>(
                                    client,
                                    productId,
                                    member.getIdentifier(),
                                    member.getReturnClass()
                            )
                    );
                    break;
                }
            }

        });

        return new ThingProductImpl(definition.getProductId(), thingProductPropertyMap, thingProductServiceMap);
    }
}
