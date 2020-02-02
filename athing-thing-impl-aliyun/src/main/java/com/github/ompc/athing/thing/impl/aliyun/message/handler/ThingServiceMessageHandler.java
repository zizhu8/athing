package com.github.ompc.athing.thing.impl.aliyun.message.handler;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.IThingService;
import com.github.ompc.athing.thing.ThingException;
import com.github.ompc.athing.thing.impl.aliyun.message.IThingMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingReply;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.github.ompc.athing.thing.ThingCodes.REQUEST_ERROR;
import static com.github.ompc.athing.thing.ThingCodes.THING_SERVICE_NOT_PROVIDED;
import static com.github.ompc.athing.thing.impl.aliyun.message.ThingReply.failure;
import static com.github.ompc.athing.thing.impl.aliyun.message.ThingReply.success;
import static com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder.MQTT_QOS_AT_LEAST_ONCE;
import static com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder.MQTT_QOS_AT_MOST_ONCE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.replaceOnce;
import static org.apache.commons.lang3.StringUtils.startsWith;

public class ThingServiceMessageHandler implements IThingMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IThing thing;

    private final JsonParser parser = new JsonParser();
    private final Gson gson = GsonFactory.getGson();

    public ThingServiceMessageHandler(IThing thing) {
        this.thing = thing;
    }

    @Override
    public String[] getMqttTopicExpress() {
        final Set<String> topicExpress = new LinkedHashSet<>();

        // 订阅同步服务调用RRPC主题
        topicExpress.add(format("/ext/rrpc/+/sys/%s/%s/thing/service/+", thing.getProductId(), thing.getThingId()));

        // 订阅异步服务调用MQTT主题
        for (final String serviceId : thing.listThingServiceIds()) {
            topicExpress.add(format("/sys/%s/%s/thing/service/%s", thing.getProductId(), thing.getThingId(), serviceId));
        }

        return topicExpress.toArray(new String[0]);
    }

    @Override
    public void onThingMessage(String topic, String requestJson, ThingResponder responder) throws ThingException {

        final JsonObject requestJsonObject = parser.parse(requestJson).getAsJsonObject();
        final ThingServiceMessageHeader header = parseThingServiceMessageHeader(topic, requestJsonObject);

        // 获取设备服务
        final IThingService thingService = thing.getThingService(header.serviceId);
        if (null == thingService) {
            reply(responder, header, failure(header.requestId, THING_SERVICE_NOT_PROVIDED, format("service=%s not found", header.serviceId)));
            return;
        }

        // 检查服务模式
        if (!checkThingServiceMode(header, thingService)) {
            reply(responder, header, failure(header.requestId, THING_SERVICE_NOT_PROVIDED, format("service=%s mode=%s not supported",
                    header.serviceId,
                    header.isSync ? "SYNC" : "ASYNC"
            )));
            return;
        }


        // 解析调用参数
        final JsonElement parameterJsonElement = requestJsonObject.get("params");

        // 调用设备服务
        try {
            final Object result = thingService.service(parseParameterMap(parameterJsonElement, thingService));
            reply(responder, header, success(header.requestId, "success", result));
            logger.info("thing-service-executor:/{}/{}/{}/{} invoke success.",
                    thing.getProductId(),
                    thing.getThingId(),
                    header.serviceId,
                    header.requestId
            );

        } catch (Throwable cause) {
            logger.warn("thing-service-executor:/{}/{}/{}/{} invoke failure.",
                    thing.getProductId(),
                    thing.getThingId(),
                    header.serviceId,
                    header.requestId,
                    cause
            );
            reply(responder, header, failure(header.requestId, REQUEST_ERROR, cause.getMessage()));
        }

    }

    private Map<String, Object> parseParameterMap(JsonElement parameterJsonElement, IThingService thingService) {
        final Map<String, Object> parameterMap = new HashMap<>();
        parameterJsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            final Class<?> parameterType = thingService.getParameterTypeMap().get(entry.getKey());
            if (null == parameterType) {
                return;
            }
            parameterMap.put(
                    entry.getKey(),
                    gson.fromJson(entry.getValue(), parameterType)
            );
        });
        return parameterMap;
    }

    private boolean checkThingServiceMode(ThingServiceMessageHeader header, IThingService thingService) {
        return header.isSync == thingService.isSupportSynchronous()
                || !header.isSync == thingService.isSupportAsynchronous();
    }

    /**
     * 解析服务调用消息头
     *
     * @param topic             主题
     * @param requestJsonObject 请求
     * @return 服务调用消息头
     */
    private ThingServiceMessageHeader parseThingServiceMessageHeader(String topic, JsonObject requestJsonObject) {
        final String method = requestJsonObject.get("method").getAsString();
        if (!startsWith(method, "thing.service.")) {
            throw new IllegalStateException(String.format("illegal method=%s", method));
        }
        return new ThingServiceMessageHeader(
                topic,
                requestJsonObject.get("id").getAsString(),
                replaceOnce(method, "thing.service.", "")
        );
    }

    /**
     * 应答服务请求
     *
     * @param header 请求消息头
     * @param reply  设备应答
     */
    private void reply(ThingResponder responder, ThingServiceMessageHeader header, ThingReply reply) throws ThingException {
        // 同步服务不需要这么高的发布质量，等待超时即可
        final int qos = header.isSync ? MQTT_QOS_AT_MOST_ONCE : MQTT_QOS_AT_LEAST_ONCE;
        responder.reply(header.replyTopic, qos, reply);
    }


    /**
     * 设备服务调用消息头
     */
    static class ThingServiceMessageHeader {

        final String requestId;
        final String serviceId;
        final String topic;
        final boolean isSync;
        final String replyTopic;

        ThingServiceMessageHeader(String topic, String requestId, String serviceId) {
            this.requestId = requestId;
            this.serviceId = serviceId;
            this.topic = topic;
            this.isSync = topic.startsWith("/ext/rrpc");
            this.replyTopic = isSync ? topic : topic + "_reply";
        }

    }


}
