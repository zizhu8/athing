package com.github.ompc.athing.aliyun.common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Feature编解码器
 */
public class FeatureCodec {

    /**
     * 将featureMap编码为feature字符串
     *
     * @param featureMap Map of feature
     * @return feature string
     */
    public static String encode(Map<String, String> featureMap) {
        final StringBuilder featureSB = new StringBuilder("feature=1;");
        if (null != featureMap) {
            featureMap.forEach((key, value) -> {
                if (!key.equals("feature") || !value.equals("1")) {
                    featureSB.append(key).append("=").append(value).append(";");
                }
            });
        }
        return featureSB.toString();
    }

    /**
     * 将feature字符串解码为featureMap
     *
     * @param feature feature string
     * @return Map of feature
     */
    public static Map<String, String> decode(String feature) {
        final Map<String, String> featureMap = new LinkedHashMap<>();
        if (null != feature && feature.startsWith("feature=1;")) {
            Stream.of(feature.split(";"))
                    .filter(kvSeg -> !kvSeg.isEmpty())
                    .forEach(kvSeg -> {
                        final String[] kv = kvSeg.split("=");
                        if (kv.length == 2) {
                            featureMap.put(kv[0], kv[1]);
                        }
                    });
        }
        return featureMap;
    }

}
