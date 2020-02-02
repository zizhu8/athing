package com.github.ompc.athing.product.impl.aliyun;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.iot.model.v20180120.InvokeThingServiceRequest;
import com.aliyuncs.iot.model.v20180120.InvokeThingServiceResponse;
import com.aliyuncs.iot.model.v20180120.SetDevicePropertyRequest;
import com.aliyuncs.iot.model.v20180120.SetDevicePropertyResponse;
import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.product.IClientResult;
import com.github.ompc.athing.product.IThingPlatformClient;
import com.github.ompc.athing.product.ThingProductException;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.ompc.athing.product.impl.aliyun.ClientResultImpl.failure;
import static com.github.ompc.athing.product.impl.aliyun.ClientResultImpl.success;
import static com.github.ompc.athing.product.impl.aliyun.util.TokenUtils.computeToken;

class ThingPlatformClientImpl implements IThingPlatformClient {

    private final IAcsClient client;
    private final Gson gson = GsonFactory.getGson();

    public ThingPlatformClientImpl(IAcsClient client) {
        this.client = client;
    }

    @Override
    public IClientResult<Void> assignThingPropertyValue(String productId, String thingId, String propertyId, Object value) throws ThingProductException {
        final SetDevicePropertyRequest request = new SetDevicePropertyRequest();
        request.setProductKey(productId);
        request.setDeviceName(thingId);

        final Map<String, Object> itemMap = new LinkedHashMap<>();
        itemMap.put(propertyId, value);
        request.setItems(gson.toJson(itemMap));

        final SetDevicePropertyResponse response;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException cause) {
            throw new ThingProductException(
                    productId,
                    String.format("assign property occur error, thing=%s;property=%s;", thingId, propertyId),
                    cause
            );
        }

        if (!response.getSuccess()) {
            return failure(response.getCode(), response.getErrorMessage());
        }

        return success(
                response.getCode(),
                response.getErrorMessage(),
                null,
                computeToken(productId, thingId, response.getData().getMessageId()));
    }

    @Override
    public <R> IClientResult<R> syncInvokeThingService(String productId, String thingId, String serviceId, Class<R> returnType, Object parameter) throws ThingProductException {
        final InvokeThingServiceRequest request = new InvokeThingServiceRequest();
        request.setProductKey(productId);
        request.setDeviceName(thingId);
        request.setIdentifier(serviceId);
        request.setArgs(gson.toJson(parameter));

        final InvokeThingServiceResponse response;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException cause) {
            throw new ThingProductException(
                    productId,
                    String.format("invoke service occur error, thing=%s;service=%s", thingId, serviceId),
                    cause
            );
        }

        if (!response.getSuccess()) {
            return failure(response.getCode(), response.getErrorMessage());
        }

        final String resultJson = response.getData().getResult();
        final R result = gson.fromJson(resultJson, returnType);
        return success(
                response.getCode(),
                response.getErrorMessage(),
                result,
                computeToken(productId, thingId, response.getData().getMessageId()));
    }

    @Override
    public IClientResult<Void> asyncInvokeThingService(String productId, String thingId, String serviceId, Object parameter) throws ThingProductException {
        return syncInvokeThingService(productId, thingId, serviceId, Void.class, parameter);
    }

}
