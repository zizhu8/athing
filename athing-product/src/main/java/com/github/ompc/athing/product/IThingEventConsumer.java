package com.github.ompc.athing.product;

import com.github.ompc.athing.product.event.IThingEventListener;

/**
 * 设备事件消费者
 */
public interface IThingEventConsumer extends AutoCloseable {

    /**
     * 设置设备事件监听器
     *
     * @param listener 事件监听器
     * @throws ThingProductException 消费事件失败
     */
    void setThingEventListener(IThingEventListener listener) throws ThingProductException;

}
