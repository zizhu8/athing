package com.github.ompc.athing.thing.impl.aliyun.message.handler;

import com.github.ompc.athing.aliyun.common.util.GsonFactory;
import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.ThingCodes;
import com.github.ompc.athing.thing.impl.aliyun.message.IThingMessageHandler;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingReply;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingResponder;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class ThingReportReplyMessageHandler implements IThingMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IThing thing;

    private final JsonParser parser = new JsonParser();
    private final Gson gson = GsonFactory.getGson();

    public ThingReportReplyMessageHandler(IThing thing) {
        this.thing = thing;
    }

    @Override
    public String[] getMqttTopicExpress() {
        return new String[]{format("/sys/%s/%s/thing/event/+/post_reply", thing.getProductId(), thing.getThingId())};
    }

    @Override
    public void onThingMessage(String topic, String replyJson, ThingResponder responder) {
        // 解析请求参数
        final ThingReply reply = gson.fromJson(replyJson, ThingReply.class);

        // 处理应答结果
        if (ThingCodes.OK == reply.getCode()) {
            logger.info("thing-report-executor:/{}/{}/{} report reply success",
                    thing.getProductId(),
                    thing.getThingId(),
                    reply.getId()
            );
        } else {
            logger.warn("thing-report-executor:/{}/{}/{} report reply failure, code={};message={};",
                    thing.getProductId(),
                    thing.getThingId(),
                    reply.getId(),
                    reply.getCode(),
                    reply.getMessage()
            );
        }
    }
}
