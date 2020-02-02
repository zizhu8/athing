package com.github.ompc.athing.thing.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.ThingException;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 应答者
 */
public class ThingResponder {

    public static final int MQTT_QOS_AT_MOST_ONCE = 0;
    public static final int MQTT_QOS_AT_LEAST_ONCE = 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonFactory.getGson();
    private final IThing thing;
    private final IMqttClient client;

    public ThingResponder(IThing thing, IMqttClient client) {
        this.thing = thing;
        this.client = client;
    }

    /**
     * 发布消息
     *
     * @param topic   消息主题
     * @param message 消息
     * @throws ThingException 发布失败
     */
    protected void publish(String topic, int qos, Object message) throws ThingException {
        try {
            final MqttMessage mqttMessage = new MqttMessage(gson.toJson(message).getBytes(UTF_8));
            mqttMessage.setRetained(false);
            mqttMessage.setQos(qos);
            client.publish(topic, mqttMessage);
            logger.debug("thing:/{}/{} publish message success, topic={} -> {}",
                    thing.getProductId(), thing.getThingId(), topic, mqttMessage);
        } catch (Throwable cause) {
            throw new ThingException(
                    thing.getProductId(),
                    thing.getThingId(),
                    format("publish message occur error, qos=%s;topic=%s;", qos, topic),
                    cause
            );
        }
    }

    /**
     * 发布应答消息
     *
     * @param topic 应答消息主题
     * @param reply 应答消息
     * @throws ThingException 发布失败
     */
    public void reply(String topic, ThingReply reply) throws ThingException {
        reply(topic, MQTT_QOS_AT_LEAST_ONCE, reply);
    }

    public void reply(String topic, int qos, ThingReply reply) throws ThingException {
        publish(topic, qos, reply);
    }

}
