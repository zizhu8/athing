package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.definition.ThingProductMember;
import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.event.ThingReportPropertiesEvent;
import com.github.ompc.athing.product.event.ThingReportPropertiesEvent.PropertyValue;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReportMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReportPropertiesMessageHeader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.endsWith;

public class ThingReportPropertiesMessageDecoder extends ThingEventDecoder<ThingReportMessageHeader, ThingReportPropertiesMessageHeader> {

    private final ThingProductDefinition thingProductDefinition;
    private final Gson gson = GsonFactory.getGson();

    public ThingReportPropertiesMessageDecoder(ThingProductDefinition thingProductDefinition) {
        this.thingProductDefinition = thingProductDefinition;
    }

    @Override
    protected boolean matches(ThingReportMessageHeader preHeader) {
        return endsWith(preHeader.getParent().getParent().getMessageTopic(), "/thing/event/property/post");
    }

    @Override
    protected ThingReportPropertiesMessageHeader decodeHeader(ThingReportMessageHeader preHeader, JsonObject prePayloadJsonObject) {
        return new ThingReportPropertiesMessageHeader(preHeader);
    }

    @Override
    protected JsonElement decodePayload(ThingReportPropertiesMessageHeader thingReportPropertiesMessageHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return required(prePayloadJsonObject, "items");
    }

    @Override
    protected ThingEvent decodeThingEvent(ThingReportPropertiesMessageHeader thingReportPropertiesMessageHeader, JsonElement payloadJsonElement) throws ThingMessageDecodeException {
        final JsonObject itemsJsonObject = payloadJsonElement.getAsJsonObject();
        final Map<String, PropertyValue> propertyValueMap = new HashMap<>();
        try {

            for (final Entry<String, JsonElement> entry : itemsJsonObject.entrySet()) {
                final String propertyId = entry.getKey();
                final ThingProductMember thingProductMember = thingProductDefinition.getThingProductMember(
                        ThingProductMember.Type.PROPERTY,
                        propertyId
                );
                if (null == thingProductMember) {
                    continue;
                }

                final JsonObject itemJsonObject = entry.getValue().getAsJsonObject();
                final long timestamp = getMemberAsLong(itemJsonObject, "time",
                        thingReportPropertiesMessageHeader.getParent().getParent().getTimestamp());

                final Object value = gson.fromJson(itemJsonObject.get("value"), thingProductMember.getReturnClass());
                propertyValueMap.put(propertyId, new PropertyValue(value, timestamp));

            }

            return new ThingReportPropertiesEvent(
                    thingReportPropertiesMessageHeader.getParent().getParent().getProductId(),
                    thingReportPropertiesMessageHeader.getParent().getParent().getThingId(),
                    thingReportPropertiesMessageHeader.getParent().getParent().getTimestamp(),
                    propertyValueMap
            );

        } catch (Throwable cause) {
            throw new ThingMessageDecodeException(
                    String.format("decode event occur error! payload=%s", payloadJsonElement),
                    cause
            );
        }

    }
}
