package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.product.impl.aliyun.message.header.JmsMessageHeader;
import com.google.gson.JsonObject;

import javax.jms.Message;

public class JmsMessageDecoder extends ThingEventDecoder<Message, JmsMessageHeader> {

    @Override
    protected JmsMessageHeader decodeHeader(Message message, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        try {
            return new JmsMessageHeader(
                    message.getStringProperty("topic"),
                    message.getStringProperty("messageId")
            );
        } catch (Exception cause) {
            throw new ThingMessageDecodeException("decode jms-message header occur error!", cause);
        }
    }

}
