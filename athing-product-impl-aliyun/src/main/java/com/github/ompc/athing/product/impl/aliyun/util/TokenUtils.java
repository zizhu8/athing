package com.github.ompc.athing.product.impl.aliyun.util;

public class TokenUtils {

    /**
     * 计算TOKEN值
     * <p>
     * 在阿里云平台，我们采用[{@code /产品ID/设备ID/请求ID}]构造成TOKEN；
     * TOKEN能确保在7天内全局唯一
     * </p>
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param requestId 请求ID
     * @return TOKEN
     */
    public static String computeToken(String productId, String thingId, String requestId) {
        return String.format("/%s/%s/%s", productId, thingId, requestId);
    }

}
