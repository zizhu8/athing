package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.product.impl.aliyun.message.header.ThingMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReportMessageHeader;
import com.google.gson.JsonObject;

public class ThingReportMessageDecoder extends ThingEventDecoder<ThingMessageHeader, ThingReportMessageHeader> {

    @Override
    protected boolean matches(ThingMessageHeader preHeader) {
        return preHeader.getParent().getMessageTopic().matches("^/[^/]+/[^/]+/thing/event/[^/]+/post$");
    }

    @Override
    protected ThingReportMessageHeader decodeHeader(ThingMessageHeader preHeader, JsonObject prePayloadJsonObject) {
        return new ThingReportMessageHeader(preHeader);
    }

}
