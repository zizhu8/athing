package com.github.ompc.athing.product.impl;

import com.github.ompc.athing.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 产品属性实现
 *
 * @param <V> 属性值类型
 */
class ThingProductPropertyImpl<V> implements IThingProductProperty<V> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IThingPlatformClient client;
    private final String productId;
    private final String propertyId;

    public ThingProductPropertyImpl(IThingPlatformClient client, String productId, String propertyId) {
        this.client = client;
        this.productId = productId;
        this.propertyId = propertyId;
    }


    @Override
    public String getPropertyId() {
        return propertyId;
    }

    @Override
    public IThingToken<Void> set(String thingId, V value) throws ThingProductException {

        final IClientResult<Void> result = client.assignThingPropertyValue(productId, thingId, propertyId, value);
        if (!result.isSuccess()) {
            throw new ThingProductException(
                    productId,
                    String.format("assign property occur error, thing=%s;property=%s;code=%s;msg=%s;",
                            thingId, propertyId, result.getErrorCode(), result.getErrorMessage())
            );
        }
        logger.info("thing-product-property:/{}/{}/{} assign property posted, token={}",
                productId, thingId, propertyId, result.getToken());
        return new ThingTokenImpl<>(productId, thingId, result.getToken(), result.getData());

    }

}
