package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.event.IThingEventListener;
import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.impl.aliyun.message.header.JmsMessageHeader;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ThingMessageListener implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JsonParser parser = new JsonParser();
    private final ThingProductDefinition definition;
    private final IThingEventListener listener;

    private final ThingEventDecoder<Message, JmsMessageHeader> decoder;


    public ThingMessageListener(ThingProductDefinition definition, IThingEventListener listener) {
        this.definition = definition;
        this.listener = listener;
        this.decoder =
                new JmsMessageDecoder()
                        .next(new ThingMessageDecoder()
                                .next(new ThingReplyMessageDecoder()
                                        .next(new ThingReplyServiceReturnMessageDecoder(definition))
                                        .next(new ThingReplyPropertySetMessageDecoder())
                                )
                                .next(new ThingReportMessageDecoder()
                                        .next(new ThingReportPropertiesMessageDecoder(definition))
                                        .next(new ThingReportDataMessageDecoder(definition))
                                )
                        );
    }

    private String takeMessageId(Message message) {
        try {
            return message.getJMSMessageID();
        } catch (JMSException cause) {
            throw new ThingMessageRollbackException(definition.getProductId(), "take id occur error!", cause);
        }
    }

    private String takeMessageBodyJson(Message message) {
        try {
            return new String(message.getBody(byte[].class), UTF_8);
        } catch (JMSException cause) {
            throw new ThingMessageRollbackException(definition.getProductId(), "take body occur error!", cause);
        }
    }

    private ThingEvent decodeThingEvent(Message message, String payloadJson) {
        try {
            return decoder.decodeThingMessage(message, parser.parse(payloadJson));
        } catch (ThingMessageDecodeException cause) {
            throw new ThingMessageRollbackException(definition.getProductId(), "decode occur error!", cause);
        }
    }

    @Override
    public void onMessage(Message message) {

        final String productId = definition.getProductId();
        logger.debug("thing-product-jms:/{} receive jms-message={}", productId, message);

        try {

            // 获取消息ID
            final String messageId = takeMessageId(message);

            // 获取消息载荷
            final String payloadJson = takeMessageBodyJson(message);
            logger.debug("thing-product-jms:/{}/{} receive message={}", productId, messageId, payloadJson);

            // 解码消息中的事件
            final ThingEvent event;
            try {
                event = decodeThingEvent(message, payloadJson);
                if (logger.isDebugEnabled()) {
                    logger.debug("thing-product-jms:/{}/{} decode thing-event={}", productId, messageId, GsonFactory.getGson().toJson(event));
                }
            } catch (Exception cause) {
                throw new ThingMessageRollbackException(productId, messageId, "decode message occur error!", cause);
            }

            // 没有任何事件被解码出来，说明这条消息不应该被这台机器消费
            // 有可能是服务端代码版本切换原因。打回，等待正确的版本/机器消费即可
            if (null == event) {
                throw new ThingMessageRollbackException(productId, messageId, "none event decoded");
            }

            // 消费设备事件
            try {
                listener.onEvent(event);
            } catch (Exception cause) {
                throw new ThingMessageRollbackException(productId, messageId, "fire listener occur error!", cause);
            }

            // 一切阿弥陀佛，提交消费成功
            try {
                message.acknowledge();
            } catch (JMSException cause) {
                throw new ThingMessageRollbackException(productId, messageId, "ack occur error!", cause);
            }

        } catch (Throwable cause) {
            logger.warn("receive jms-message occur error!", cause);
            if (cause instanceof ThingMessageRollbackException) {
                throw (ThingMessageRollbackException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }

    }

    /**
     * 设备消息回滚
     */
    static class ThingMessageRollbackException extends RuntimeException {

        public ThingMessageRollbackException(String productId, String message) {
            super(format(productId, message));
        }

        public ThingMessageRollbackException(String productId, String message, Throwable cause) {
            super(format(productId, message), cause);
        }

        public ThingMessageRollbackException(String productId, String messageId, String message) {
            super(format(productId, message));
        }

        public ThingMessageRollbackException(String productId, String messageId, String message, Throwable cause) {
            super(format(productId, message), cause);
        }

        private static String format(String productId, String message) {
            return String.format("thing-product-jms:/%s %s", productId, message);
        }

        private static String format(String productId, String messageId, String message) {
            return String.format("thing-product-jms:/%s/%s %s", productId, messageId, message);
        }

    }

}
