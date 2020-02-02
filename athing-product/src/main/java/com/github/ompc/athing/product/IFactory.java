package com.github.ompc.athing.product;

import com.github.ompc.athing.product.definition.ThingProductDefinition;

/**
 * 工厂接口
 *
 * @param <T> 生产类型
 */
public interface IFactory<T> {

    /**
     * 根据产品定义生产
     *
     * @param definition 产品定义
     * @return 生产结果
     * @throws ThingProductException 生产失败
     */
    T make(ThingProductDefinition definition) throws ThingProductException;

}
