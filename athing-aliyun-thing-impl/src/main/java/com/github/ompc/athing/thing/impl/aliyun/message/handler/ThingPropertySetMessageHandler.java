package com.github.ompc.athing.thing.impl.aliyun.message.handler;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.IThingProperty;
import com.github.ompc.athing.thing.ThingException;
import com.github.ompc.athing.thing.impl.aliyun.message.IThingMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.github.ompc.athing.aliyun.common.util.Constants.FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS;
import static com.github.ompc.athing.thing.impl.aliyun.message.ThingReply.successWithFeatureMap;
import static java.lang.String.format;

public class ThingPropertySetMessageHandler implements IThingMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IThing thing;

    private final JsonParser parser = new JsonParser();
    private final Gson gson = GsonFactory.getGson();

    public ThingPropertySetMessageHandler(IThing thing) {
        this.thing = thing;
    }

    @Override
    public String[] getMqttTopicExpress() {
        return new String[]{
                format("/sys/%s/%s/thing/service/property/set", thing.getProductId(), thing.getThingId())
        };
    }

    @Override
    public void onThingMessage(String topic, String requestJson, ThingResponder responder) throws ThingException {

        final JsonObject requestJsonObject = parser.parse(requestJson).getAsJsonObject();

        final String requestId = requestJsonObject.get("id").getAsString();
        final String replyTopic = topic + "_reply";

        final JsonObject parameterJsonObject = requestJsonObject.get("params").getAsJsonObject();
        final Set<String> successPropertyIds = new LinkedHashSet<>();

        // 单个属性设置失败不影响全局
        for (final Map.Entry<String, JsonElement> entry : parameterJsonObject.entrySet()) {

            // 获取设备属性
            final IThingProperty thingProperty = thing.getThingProperty(entry.getKey());
            if (null == thingProperty) {
                logger.warn("thing-property-executor:/{}/{}/{} set property ignored, property={} not found.",
                        thing.getProductId(), thing.getThingId(), requestId, entry.getKey());
                continue;
            }

            if (!thingProperty.isSupportPropertySet()) {
                logger.warn("thing-property-executor:/{}/{}/{} set property ignored, property={} set is not support set.",
                        thing.getProductId(), thing.getThingId(), requestId, entry.getKey());
                continue;
            }

            // 解析并设置属性
            // 这里使用try..catch主要是单个属性设置失败不能影响全部的属性设置
            try {
                thingProperty.set(gson.fromJson(entry.getValue(), thingProperty.getPropertyType()));
                successPropertyIds.add(thingProperty.getPropertyId());
                logger.info("thing-property-executor:/{}/{}/{} set property success, property={} change value to -> {}",
                        thing.getProductId(), thing.getThingId(), requestId, entry.getKey(), entry.getValue());
            } catch (Throwable cause) {
                logger.warn("thing-property-executor:/{}/{}/{} set property failure, property={} execute set occur error!",
                        thing.getProductId(), thing.getThingId(), requestId, entry.getKey(), cause);
            }//try

        }// for


        final Map<String, String> featureMap = new HashMap<>();
        featureMap.put(
                FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS,
                StringUtils.join(successPropertyIds, ",")
        );
        // 应答云端
        responder.reply(replyTopic, successWithFeatureMap(requestId, featureMap));

    }

}
