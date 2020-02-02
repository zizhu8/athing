package com.github.ompc.athing.product.event;

/**
 * 设备事件监听器
 */
public interface IThingEventListener {

    /**
     * 设备事件到达
     *
     * @param event 设备事件
     * @throws Exception 消费事件失败
     */
    void onEvent(ThingEvent event) throws Exception;

}
