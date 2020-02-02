package com.github.ompc.athing.product;

/**
 * 产品异常
 */
public class ThingProductException extends Exception {

    private final String productId;

    public ThingProductException(String productId, String message) {
        super(format(productId, message));
        this.productId = productId;
    }

    public ThingProductException(String productId, String message, Throwable cause) {
        super(format(productId, message), cause);
        this.productId = productId;
    }

    private static String format(String productId, String message) {
        return String.format("thing-product=%s %s", productId, message);
    }

    public String getProductId() {
        return productId;
    }

}
