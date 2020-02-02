package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.event.ThingReplyPropertySetEvent;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReplyMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReplyPropertiesSetMessageHeader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.ompc.athing.aliyun.common.util.Constants.FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS;
import static com.github.ompc.athing.product.impl.aliyun.util.TokenUtils.computeToken;


public class ThingReplyPropertySetMessageDecoder extends ThingEventDecoder<ThingReplyMessageHeader, ThingReplyPropertiesSetMessageHeader> {

    @Override
    protected ThingReplyPropertiesSetMessageHeader decodeHeader(ThingReplyMessageHeader preHeader, JsonObject prePayloadJsonObject) {
        return new ThingReplyPropertiesSetMessageHeader(preHeader);
    }

    private Set<String> parsePropertyIds(ThingReplyPropertiesSetMessageHeader header) {
        final Set<String> propertyIds = new HashSet<>();
        final Map<String, String> featureMap = header.getParent().getFeatureMap();
        final String stringPropertyIds = featureMap.get(FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS);
        if (StringUtils.isNotBlank(stringPropertyIds)) {
            for (final String propertyId : StringUtils.split(stringPropertyIds, ",")) {
                if (StringUtils.isNotBlank(propertyId)) {
                    propertyIds.add(propertyId);
                }
            }
        }
        return propertyIds;
    }

    @Override
    protected JsonElement decodePayload(ThingReplyPropertiesSetMessageHeader thingReplyPropertiesSetMessageHeader, JsonObject prePayloadJsonObject) {
        return prePayloadJsonObject;
    }

    @Override
    protected ThingEvent decodeThingEvent(ThingReplyPropertiesSetMessageHeader thingReplyPropertiesSetMessageHeader, JsonElement payloadJsonElement) {
        final String productId = thingReplyPropertiesSetMessageHeader.getParent().getParent().getProductId();
        final String thingId = thingReplyPropertiesSetMessageHeader.getParent().getParent().getThingId();
        final String requestId = thingReplyPropertiesSetMessageHeader.getParent().getRequestId();
        return new ThingReplyPropertySetEvent(
                productId,
                thingId,
                thingReplyPropertiesSetMessageHeader.getParent().getParent().getTimestamp(),
                computeToken(productId, thingId, requestId),
                thingReplyPropertiesSetMessageHeader.getParent().getCode(),
                thingReplyPropertiesSetMessageHeader.getParent().getMessage(),
                parsePropertyIds(thingReplyPropertiesSetMessageHeader)
        );
    }
}
