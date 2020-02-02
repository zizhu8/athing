package com.github.ompc.athing.product.impl.aliyun.message.header;

import java.util.Map;

public class ThingReplyMessageHeader {

    private final ThingMessageHeader parent;
    private final String topic;
    private final String requestId;
    private final int code;
    private final String message;
    private final Map<String, String> featureMap;

    public ThingReplyMessageHeader(ThingMessageHeader parent, String topic, String requestId, int code, String message, Map<String, String> featureMap) {
        this.parent = parent;
        this.topic = topic;
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.featureMap = featureMap;
    }

    public ThingMessageHeader getParent() {
        return parent;
    }

    public String getTopic() {
        return topic;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }
}
