package com.github.ompc.athing.thing;

/**
 * 设备异常
 */
public class ThingException extends Exception {

    private final String productId;
    private final String thingId;

    /**
     * 构造异常
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param message   异常信息
     * @param cause     具体异常
     */
    public ThingException(final String productId,
                          final String thingId,
                          final String message,
                          final Throwable cause) {
        super(format(productId, thingId, message), cause);
        this.productId = productId;
        this.thingId = thingId;
    }

    /**
     * 构造异常
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param message   异常信息
     */
    public ThingException(final String productId,
                          final String thingId,
                          final String message) {
        super(message);
        this.productId = productId;
        this.thingId = thingId;
    }

    private static String format(String productId, String thingId, String message) {
        return String.format("thing:/%s/%s %s", productId, thingId, message);
    }

    /**
     * 获取产品ID
     *
     * @return 产品ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    public String getThingId() {
        return thingId;
    }
}
