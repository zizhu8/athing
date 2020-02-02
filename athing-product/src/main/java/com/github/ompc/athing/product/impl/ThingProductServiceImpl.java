package com.github.ompc.athing.product.impl;

import com.github.ompc.athing.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 产品服务实现
 *
 * @param <R> 返回类型
 */
class ThingProductServiceImpl<R> implements IThingProductService<Object, R> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IThingPlatformClient client;
    private final String productId;
    private final String serviceId;
    private final Class<R> returnType;

    public ThingProductServiceImpl(final IThingPlatformClient client,
                                   final String productId,
                                   final String serviceId,
                                   final Class<R> returnType) {
        this.client = client;
        this.productId = productId;
        this.serviceId = serviceId;
        this.returnType = returnType;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public IThingToken<R> service(String thingId, Object parameter) throws ThingProductException {
        final IClientResult<R> result = client.syncInvokeThingService(productId, thingId, serviceId, returnType, parameter);
        if (!result.isSuccess()) {
            throw new ThingProductException(
                    productId,
                    String.format("invoke service occur error, thing=%s;service=%s;code=%s;msg=%s;",
                            thingId, serviceId, result.getErrorCode(), result.getErrorMessage())
            );
        }
        logger.info("thing-product-service:/{}/{}/{} invoke service success/posted, token={};",
                productId, thingId, serviceId, result.getToken());
        return new ThingTokenImpl<>(productId, thingId, result.getToken(), result.getData());

    }
}
