package com.github.ompc.athing.product.impl.aliyun;

import com.aliyuncs.IAcsClient;
import com.github.ompc.athing.product.IFactory;
import com.github.ompc.athing.product.IThingPlatformClient;
import com.github.ompc.athing.product.definition.ThingProductDefinition;

public class ThingPlatformClientFactory implements IFactory<IThingPlatformClient> {

    private final IThingPlatformClient client;

    public ThingPlatformClientFactory(IAcsClient acsClient) {
        this.client = new ThingPlatformClientImpl(acsClient);
    }

    @Override
    public IThingPlatformClient make(ThingProductDefinition definition) {
        return client;
    }

}
