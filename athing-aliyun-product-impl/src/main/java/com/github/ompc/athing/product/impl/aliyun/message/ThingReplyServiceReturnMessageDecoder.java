package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.definition.ThingProductMember;
import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.event.ThingReplyServiceReturnEvent;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReplyMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReplyServiceReturnMessageHeader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static com.github.ompc.athing.product.impl.aliyun.util.TokenUtils.computeToken;

public class ThingReplyServiceReturnMessageDecoder extends ThingEventDecoder<ThingReplyMessageHeader, ThingReplyServiceReturnMessageHeader> {

    private final ThingProductDefinition thingProductDefinition;
    private final Gson gson = GsonFactory.getGson();

    public ThingReplyServiceReturnMessageDecoder(ThingProductDefinition thingProductDefinition) {
        this.thingProductDefinition = thingProductDefinition;
    }

    @Override
    protected boolean matches(ThingReplyMessageHeader preHeader) {
        return preHeader.getTopic().matches("^/sys/[^/]+/[^/]+/thing/service/[^/]+_reply$");
    }

    private String parseServiceId(ThingReplyMessageHeader preHeader) throws ThingMessageDecodeException {
        try {
            final String topic = preHeader.getTopic();
            return topic.substring(topic.lastIndexOf("/") + 1, topic.lastIndexOf("_reply"));
        } catch (Exception cause) {
            throw new ThingMessageDecodeException("illegal service in topic=" + preHeader.getTopic(), cause);
        }
    }

    @Override
    protected ThingReplyServiceReturnMessageHeader decodeHeader(ThingReplyMessageHeader preHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return new ThingReplyServiceReturnMessageHeader(
                preHeader,
                parseServiceId(preHeader)
        );
    }

    @Override
    protected JsonElement decodePayload(ThingReplyServiceReturnMessageHeader thingReplyServiceReturnMessageHeader, JsonObject prePayloadJsonObject) {
        return prePayloadJsonObject.get("data");
    }

    @Override
    protected ThingEvent decodeThingEvent(ThingReplyServiceReturnMessageHeader thingReplyServiceReturnMessageHeader, JsonElement payloadJsonElement) throws ThingMessageDecodeException {
        final ThingProductMember thingProductMember = thingProductDefinition.getThingProductMember(
                ThingProductMember.Type.SERVICE,
                thingReplyServiceReturnMessageHeader.getServiceId()
        );

        // 服务未定义
        if (null == thingProductMember) {
            return null;
        }
        try {
            final String productId = thingReplyServiceReturnMessageHeader.getParent().getParent().getProductId();
            final String thingId = thingReplyServiceReturnMessageHeader.getParent().getParent().getThingId();
            final String requestId = thingReplyServiceReturnMessageHeader.getParent().getRequestId();
            final Object serviceReturnObject = gson.fromJson(payloadJsonElement, thingProductMember.getReturnClass());
            return new ThingReplyServiceReturnEvent(
                    productId,
                    thingId,
                    thingReplyServiceReturnMessageHeader.getParent().getParent().getTimestamp(),
                    computeToken(productId, thingId, requestId),
                    thingReplyServiceReturnMessageHeader.getParent().getCode(),
                    thingReplyServiceReturnMessageHeader.getParent().getMessage(),
                    thingReplyServiceReturnMessageHeader.getServiceId(),
                    serviceReturnObject
            );
        } catch (Throwable cause) {
            throw new ThingMessageDecodeException(
                    String.format("decode event occur error! payload=%s", payloadJsonElement),
                    cause
            );
        }
    }

}
