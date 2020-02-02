package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.product.impl.aliyun.message.header.JmsMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingMessageHeader;
import com.google.gson.JsonObject;

public class ThingMessageDecoder extends ThingEventDecoder<JmsMessageHeader, ThingMessageHeader> {

    @Override
    protected ThingMessageHeader decodeHeader(JmsMessageHeader preHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return new ThingMessageHeader(
                preHeader,
                required(prePayloadJsonObject, "productKey").getAsString(),
                required(prePayloadJsonObject, "deviceName").getAsString(),
                getMemberAsLong(prePayloadJsonObject, "gmtCreate", System.currentTimeMillis())
        );
    }

}
