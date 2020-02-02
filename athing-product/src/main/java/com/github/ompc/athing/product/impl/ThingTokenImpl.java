package com.github.ompc.athing.product.impl;

import com.github.ompc.athing.product.IThingToken;

/**
 * 应答令牌实现
 *
 * @param <E> 令牌携带数据类型
 */
class ThingTokenImpl<E> implements IThingToken<E> {

    private final String productId;
    private final String thingId;
    private final String token;
    private final E data;

    public ThingTokenImpl(String productId, String thingId, String token, E data) {
        this.productId = productId;
        this.thingId = thingId;
        this.token = token;
        this.data = data;
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
    public String getToken() {
        return token;
    }

    @Override
    public E getData() {
        return data;
    }

}
