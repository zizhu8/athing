package com.github.ompc.athing.thing.impl.aliyun.message;

import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.IThingProperty;
import com.github.ompc.athing.thing.ThingException;
import com.github.ompc.athing.thing.impl.aliyun.message.handler.ThingPropertySetMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.message.handler.ThingReportReplyMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.message.handler.ThingServiceMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.util.MapObject;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder.MQTT_QOS_AT_LEAST_ONCE;
import static com.github.ompc.athing.thing.impl.aliyun.util.StringUtils.generateSequenceId;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;

/**
 * 设备消息处理
 */
public class ThingMessageProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IThing thing;
    private final IMqttClient client;
    private final ExecutorService workers;
    private final ThingResponder thingResponder;
    private final Collection<IThingMessageHandler> handlers = new LinkedHashSet<>();

    public ThingMessageProcessor(IThing thing, IMqttClient client, ExecutorService workers) throws ThingException {
        this.thing = thing;
        this.client = client;
        this.workers = workers;
        this.thingResponder = new ThingResponder(thing, client);
        initThingMessageHandler();
        subscribeMqttTopic();
    }


    /**
     * 初始化设备消息处理器
     */
    private void initThingMessageHandler() {
        handlers.add(new ThingPropertySetMessageHandler(thing));
        handlers.add(new ThingReportReplyMessageHandler(thing));
        handlers.add(new ThingServiceMessageHandler(thing));
    }


    /**
     * 订阅MQTT消息主题
     *
     * @throws ThingException 订阅失败
     */
    private void subscribeMqttTopic() throws ThingException {
        for (final IThingMessageHandler handler : handlers) {
            for (final String topicExpress : handler.getMqttTopicExpress()) {
                subscribe(topicExpress, (topic, mqttMessage) -> workers.submit(() -> {
                    final String message = new String(mqttMessage.getPayload(), UTF_8);
                    try {
                        handler.onThingMessage(topic, message, thingResponder);
                    } catch (ThingException e) {
                        logger.warn("thing:/{}/{} handle thing-message failure, topic={};message={};",
                                thing.getProductId(),
                                thing.getThingId(),
                                topic,
                                message
                        );
                    }
                }));
            }
        }
    }

    /**
     * 订阅主题
     *
     * @param topic    主题
     * @param listener 消息监听器
     * @throws ThingException 订阅主题失败
     */
    private void subscribe(String topic, IMqttMessageListener listener) throws ThingException {
        try {
            client.subscribe(topic, listener);
            logger.info("thing:/{}/{} subscribe topic success, topic={};listener={}",
                    thing.getProductId(), thing.getThingId(), topic, listener);
        } catch (MqttException cause) {
            throw new ThingException(
                    thing.getProductId(),
                    thing.getThingId(),
                    format("subscribe topic occur error, topic=%s;", topic),
                    cause
            );
        }
    }

    // 执行报告投递
    private String report(final String dataId, MapObject dataMap) throws ThingException {
        final String requestId;
        thingResponder.publish(String.format("/sys/%s/%s/thing/event/%s/post", thing.getProductId(), thing.getThingId(), dataId),
                MQTT_QOS_AT_LEAST_ONCE,
                new MapObject()
                        .putProperty("id", requestId = generateSequenceId())
                        .putProperty("version", "1.0")
                        .putProperty("method", String.format("thing.event.%s.post", dataId))
                        .putProperty("params", dataMap)
        );
        return requestId;
    }

    /**
     * 报告设备数据
     *
     * @param dataId 数据ID
     * @param data   数据
     * @param <E>    数据类型
     * @throws ThingException 报告失败
     */
    public <E> void reportThingData(final String dataId, final E data) throws ThingException {
        final String requestId =
                report(dataId, new MapObject()
                        .putProperty("time", System.currentTimeMillis())
                        .putProperty("value", data));
        logger.info("thing-report-executor:/{}/{}/{} report posted, report-id={}",
                thing.getProductId(),
                thing.getThingId(),
                requestId,
                dataId
        );
    }

    /**
     * 报告设备属性
     *
     * @param propertyIds 指定属性ID集合
     * @throws ThingException 报告失败
     */
    public void reportThingProperties(String... propertyIds) throws ThingException {
        final MapObject parameterMap = new MapObject();
        for (final IThingProperty property : Stream.of(propertyIds)
                .map(thing::getThingProperty)
                .filter(Objects::nonNull)
                .filter(IThingProperty::isSupportReport)
                .collect(toSet())) {
            parameterMap.enterProperty(property.getPropertyId())
                    .putProperty("value", property.report())
                    .putProperty("time", System.currentTimeMillis())
            ;
        }
        final String requestId = report("property", parameterMap);
        logger.info("thing-report-executor:/{}/{}/{} report posted, report-id=property;propertyIds={}",
                thing.getProductId(),
                thing.getThingId(),
                requestId,
                parameterMap.keySet()
        );
    }

}
