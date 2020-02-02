package com.github.ompc.athing.thing.impl.aliyun.message;

import com.github.ompc.athing.thing.ThingException;

/**
 * 设备消息处理器
 */
public interface IThingMessageHandler {

    /**
     * 获取处理器订阅的MQTT-TOPIC列表
     *
     * @return TOPIC列表
     */
    String[] getMqttTopicExpress();

    /**
     * 处理设备消息
     *
     * @param topic     MQTT消息主题
     * @param message   消息
     * @param responder 应答器
     * @throws ThingException 处理失败
     */
    void onThingMessage(String topic, String message, ThingResponder responder) throws ThingException;

}
