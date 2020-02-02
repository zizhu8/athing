package com.github.ompc.athing.product.impl.aliyun.message.header;

public class JmsMessageHeader {

    private final String messageTopic;
    private final String messageId;

    public JmsMessageHeader(String messageTopic, String messageId) {
        this.messageTopic = messageTopic;
        this.messageId = messageId;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public String getMessageId() {
        return messageId;
    }
}
