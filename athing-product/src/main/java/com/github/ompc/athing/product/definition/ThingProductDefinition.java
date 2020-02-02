package com.github.ompc.athing.product.definition;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 产品定义
 */
public class ThingProductDefinition implements Iterable<ThingProductMember> {

    private final String productId;
    private final Map<ThingProductMember.Key, ThingProductMember> thingProductMemberMap
            = new ConcurrentHashMap<>();

    public ThingProductDefinition(String productId) {
        this.productId = productId;
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
     * 定义成员
     *
     * @param member 成员
     * @return this
     */
    public ThingProductDefinition defineThingProductMember(ThingProductMember member) {
        thingProductMemberMap.put(member.getKey(), member);
        return this;
    }

    /**
     * 获取成员
     *
     * @param type       成员类型
     * @param identifier 成员ID
     * @return 成员
     */
    public ThingProductMember getThingProductMember(ThingProductMember.Type type, String identifier) {
        return thingProductMemberMap.get(new ThingProductMember.Key(type, identifier));
    }

    @Override
    public Iterator<ThingProductMember> iterator() {
        return thingProductMemberMap.values().iterator();
    }

}
