package com.github.ompc.athing.thing;

/**
 * 设备返回码常量
 */
public class ThingCodes {

    /**
     * 成功
     */
    public static final int OK = 200;

    /**
     * 内部错误，处理请求时发生内部错误
     */
    public static final int REQUEST_ERROR = 400;

    /**
     * 请求参数错误，入参校验失败
     */
    public static final int REQUEST_PARAMETER_ERROR = 460;

    /**
     * 请求过于频繁，设备处理不过来
     */
    public static final int TOO_MANY_REQUEST = 429;

    /**
     * 设备服务尚未定义
     */
    public static final int THING_SERVICE_NOT_PROVIDED = 5161;

    /**
     * 设备属性尚未定义
     */
    public static final int THING_PROPERTY_NOT_PROVIDED = 5159;

    /**
     * 设备报告尚未定义
     */
    public static final int THING_REPORT_NOT_PROVIDED = 5160;

}
