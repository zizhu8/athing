package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.definition.ThingProductMember;
import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.event.ThingReportDataEvent;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReportDataMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReportMessageHeader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ThingReportDataMessageDecoder extends ThingEventDecoder<ThingReportMessageHeader, ThingReportDataMessageHeader> {

    private final ThingProductDefinition thingProductDefinition;
    private final Gson gson = GsonFactory.getGson();

    public ThingReportDataMessageDecoder(ThingProductDefinition thingProductDefinition) {
        this.thingProductDefinition = thingProductDefinition;
    }

    @Override
    protected ThingReportDataMessageHeader decodeHeader(ThingReportMessageHeader preHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return new ThingReportDataMessageHeader(
                preHeader,
                required(prePayloadJsonObject, "identifier").getAsString(),
                getMemberAsLong(prePayloadJsonObject, "time", preHeader.getParent().getTimestamp())
        );
    }

    @Override
    protected JsonElement decodePayload(ThingReportDataMessageHeader thingReportDataMessageHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return required(prePayloadJsonObject, "value");
    }

    @Override
    protected ThingEvent decodeThingEvent(ThingReportDataMessageHeader thingReportDataMessageHeader, JsonElement payloadJsonElement) throws ThingMessageDecodeException {
        final ThingProductMember thingProductMember = thingProductDefinition.getThingProductMember(
                ThingProductMember.Type.DATA,
                thingReportDataMessageHeader.getReportId()
        );
        // 数据未定义
        if (null == thingProductMember) {
            return null;
        }

        try {

            final Object data = gson.fromJson(payloadJsonElement, thingProductMember.getReturnClass());
            return new ThingReportDataEvent(
                    thingReportDataMessageHeader.getParent().getParent().getProductId(),
                    thingReportDataMessageHeader.getParent().getParent().getThingId(),
                    thingReportDataMessageHeader.getParent().getParent().getTimestamp(),
                    thingReportDataMessageHeader.getReportId(),
                    thingReportDataMessageHeader.getCollectionTimestamp(),
                    data
            );
        } catch (Throwable cause) {
            throw new ThingMessageDecodeException(
                    String.format("decode event occur error! payload=%s", payloadJsonElement),
                    cause
            );
        }
    }


}
