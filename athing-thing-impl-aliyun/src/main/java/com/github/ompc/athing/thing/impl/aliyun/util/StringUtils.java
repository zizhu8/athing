package com.github.ompc.athing.thing.impl.aliyun.util;

import static java.lang.System.currentTimeMillis;

public class StringUtils {

    /**
     * 字节数组转16进制字符串
     *
     * @param bArray 目标字节数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(final byte[] bArray) {
        final StringBuilder sb = new StringBuilder(bArray.length * 2);
        for (byte b : bArray)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }

    /**
     * 生成SEQUENCE-ID（内容为数字的字符串），确保在设备维度唯一
     *
     * @return ID序列
     */
    public static String generateSequenceId() {

        // 事件戳做种子
        final String seed = String.valueOf(currentTimeMillis());

        // 确保7天内不会重复，7*24*3600=604800(秒)，所以只要取种子的后9位数字即可
        return seed.substring(seed.length() - 7);
    }

}
