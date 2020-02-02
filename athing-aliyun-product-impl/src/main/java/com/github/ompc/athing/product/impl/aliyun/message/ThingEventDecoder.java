package com.github.ompc.athing.product.impl.aliyun.message;

import com.github.ompc.athing.product.event.ThingEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 设备消息解码器
 *
 * @param <PRE>    前序消息头类型
 * @param <HEADER> 消息头类型
 */
public abstract class ThingEventDecoder<PRE, HEADER> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Collection<ThingEventDecoder<HEADER, ?>> nextDecoders = new ArrayList<>();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * 解码设备消息
     *
     * @param preHeader             前序消息头
     * @param prePayloadJsonElement 前序消息载荷
     * @return 解码成功返回对应的事件，否则返回null
     * @throws ThingMessageDecodeException 解码失败
     */
    final public ThingEvent decodeThingMessage(PRE preHeader, JsonElement prePayloadJsonElement) throws ThingMessageDecodeException {

        logger.debug("enter decoder:{} -> {}", this, prePayloadJsonElement);

        // 判断是否需要处理
        if (!matches(preHeader)) {
            logger.debug("exit by not matches, decoder={}", this);
            return null;
        }

        final JsonObject prePayloadJsonObject = prePayloadJsonElement.getAsJsonObject();

        // 解码消息头
        // 如果解码不出，则说明已经不是当前解码链能继续处理，需要中断解码链路
        final HEADER header = decodeHeader(preHeader, prePayloadJsonObject);
        if (null == header) {
            logger.debug("exit by header is null, decoder={}", this);
            return null;
        }

        // 解码载荷
        // 如果解码不出，则说明已经不是当前解码链能处理，需要中断解码链路
        final JsonElement payloadJsonElement = decodePayload(header, prePayloadJsonObject);
        if (null == payloadJsonElement) {
            logger.debug("exit by payload is null, decoder={}", this);
            return null;
        }

        // 解码事件
        // 如果解码不出，则说明已经不是当前解码器能处理，需要进入下一个解码器
        ThingEvent event = decodeThingEvent(header, payloadJsonElement);
        if (null != event) {
            logger.debug("exit by event decoded, event={};decoder={};", event.getEventType(), this);
            return event;
        }


        // 当前解码器解码不出，交给下一个解码器处理
        for (final ThingEventDecoder<HEADER, ?> nextDecoder : nextDecoders) {
            if ((event = nextDecoder.decodeThingMessage(header, payloadJsonElement)) != null) {
                return event;
            }
        }

        // 所有解码器均失败，解码失败
        return null;
    }

    /**
     * 是否匹配当前解码器
     *
     * @param preHeader 前序消息头
     * @return TRUE | FALSE
     */
    protected boolean matches(PRE preHeader) {
        return true;
    }

    /**
     * 解码消息头
     *
     * @param preHeader            前序消息头
     * @param prePayloadJsonObject 前序消息载荷
     * @return 消息头
     * @throws ThingMessageDecodeException 解码消息头失败
     */
    protected abstract HEADER decodeHeader(PRE preHeader, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException;

    /**
     * 解码消息载荷
     *
     * @param header               消息头
     * @param prePayloadJsonObject 前序消息载荷
     * @return 消息载荷
     * @throws ThingMessageDecodeException 解码载荷失败
     */
    protected JsonElement decodePayload(HEADER header, JsonObject prePayloadJsonObject) throws ThingMessageDecodeException {
        return prePayloadJsonObject;
    }

    /**
     * 解码事件
     *
     * @param header             消息头
     * @param payloadJsonElement 消息载荷
     * @return 事件
     * @throws ThingMessageDecodeException 解码事件失败
     */
    protected ThingEvent decodeThingEvent(HEADER header, JsonElement payloadJsonElement) throws ThingMessageDecodeException {
        return null;
    }


    /**
     * 设置下一解码节点
     *
     * @param nextDecoder 下一解码节点
     * @return this
     */
    final public ThingEventDecoder<PRE, HEADER> next(ThingEventDecoder<HEADER, ?> nextDecoder) {
        this.nextDecoders.add(nextDecoder);
        return this;
    }

    final protected JsonElement required(JsonObject objectJsonObject, String member) throws ThingMessageDecodeException {
        final JsonElement memberJsonElement = objectJsonObject.get(member);
        if (null == memberJsonElement) {
            throw new ThingMessageDecodeException(String.format("%s is required in %s", member, objectJsonObject));
        }
        return memberJsonElement;
    }

    final protected String getMemberAsString(JsonObject objectJsonObject, String member, String value) {
        return objectJsonObject.has(member)
                ? objectJsonObject.get(member).getAsString()
                : value;
    }

    final protected long getMemberAsLong(JsonObject objectJsonObject, String member, long value) {
        return objectJsonObject.has(member)
                ? objectJsonObject.get(member).getAsLong()
                : value;
    }

}
