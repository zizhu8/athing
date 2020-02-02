package com.github.ompc.athing.product.impl.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;

/**
 * ThingAcsClient
 */
public class ThingAcsClient extends DefaultAcsClient implements IAcsClient {

    /**
     * thing-acs-client
     *
     * @param regionId        region-id
     * @param accessKeyId     access-key-id
     * @param accessKeySecret access-key-secret
     */
    public ThingAcsClient(String regionId, String accessKeyId, String accessKeySecret) {
        super(DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret));
        DefaultProfile.addEndpoint(
                regionId,
                "Iot",
                String.format("iot.%s.aliyuncs.com", regionId)
        );
    }

}
