package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.FeatureCodec;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingMessageHeader;
import com.github.ompc.athing.product.impl.aliyun.message.header.ThingReplyMessageHeader;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.endsWith;

public class ThingReplyMessageDecoder extends ThingEventDecoder<ThingMessageHeader, ThingReplyMessageHeader> {

    @Override
    protected boolean matches(ThingMessageHeader preHeader) {
        return endsWith(preHeader.getParent().getMessageTopic(), "/thing/downlink/reply/message");
    }

    private Map<String, String> parseFeatureMap(int code, String features) {

        final Map<String, String> featureMap = new HashMap<>();

        // 如果不是成功的消息，message中是错误信息，不需要解析
        // 如果message不是以特定字符串开头，说明不是feature特征字符串，不需要解析
        if (code != 200//ThingCodes.OK
                || !StringUtils.startsWith(features, "feature=1;")) {
            return featureMap;
        }

        return FeatureCodec.decode(features);
    }

    @Override
    protected ThingReplyMessageHeader decodeHeader(ThingMessageHeader preHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        final int code = required(prePayloadJsonObject, "code").getAsInt();
        final String message = getMemberAsString(prePayloadJsonObject, "message", "success");
        return new ThingReplyMessageHeader(
                preHeader,
                required(prePayloadJsonObject, "topic").getAsString(),
                required(prePayloadJsonObject, "requestId").getAsString(),
                code,
                message,
                parseFeatureMap(code, message)
        );
    }

}
