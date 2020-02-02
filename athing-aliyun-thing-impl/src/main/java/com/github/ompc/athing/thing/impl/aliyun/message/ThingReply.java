package com.github.ompc.athing.thing.impl.aliyun.message;

import com.github.ompc.athing.aliyun.common.util.FeatureCodec;
import com.github.ompc.athing.thing.ThingCodes;

import java.util.Map;

/**
 * 设备应答
 */
public class ThingReply {

    private final String id;
    private final int code;
    private final Object data;
    private final String message;

    private ThingReply(String id, int code, String message, Object data) {
        this.id = id;
        this.code = code;
        this.message = null == message ? "" : message;
        this.data = null == data ? new Object() : data;
    }

    /**
     * 成功
     *
     * @param id      应答ID，与请求ID相等
     * @param message 应答消息
     * @return 设备应答
     */
    public static ThingReply success(String id, String message) {
        return success(id, message, null);
    }

    /**
     * 成功（携带featureMap）
     *
     * @param id         应答ID，与请求ID相等
     * @param featureMap featureMap
     * @return 设备应答
     */
    public static ThingReply successWithFeatureMap(String id, Map<String, String> featureMap) {
        return success(id, FeatureCodec.encode(featureMap));
    }

    /**
     * 成功
     *
     * @param id      应答ID，与请求ID相等
     * @param message 应答消息
     * @param data    应答携带数据
     * @return 设备应答
     */
    public static ThingReply success(String id, String message, Object data) {
        return new ThingReply(id, ThingCodes.OK, message, data);
    }

    /**
     * 失败
     *
     * @param id      应答ID，与请求ID相等
     * @param code    设备返回码
     * @param message 应答消息
     * @return 设备应答
     */
    public static ThingReply failure(String id, int code, String message) {
        return new ThingReply(id, code, message, null);
    }

    /**
     * 获取设备应答ID，与请求ID相等
     *
     * @return 设备应答ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取设备返回码
     *
     * @return 设备返回码
     * @see com.github.ompc.athing.thing.ThingCodes
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取应答携带数据
     *
     * @return 应答携带数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 获取应答消息
     *
     * @return 应答消息
     */
    public String getMessage() {
        return message;
    }
}
