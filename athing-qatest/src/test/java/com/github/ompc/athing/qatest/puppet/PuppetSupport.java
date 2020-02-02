package com.github.ompc.athing.qatest.puppet;

import com.github.ompc.athing.product.IThingProduct;
import com.github.ompc.athing.product.ThingProductException;
import com.github.ompc.athing.product.event.ThingEvent;
import com.github.ompc.athing.product.impl.ThingProductTemplate;
import com.github.ompc.athing.qatest.QaTestSupport;
import com.github.ompc.athing.qatest.puppet.domain.ContactInfo;
import com.github.ompc.athing.qatest.puppet.domain.PersonalInfo;
import com.github.ompc.athing.qatest.puppet.domain.Result;
import com.github.ompc.athing.qatest.puppet.domain.Tick;
import com.github.ompc.athing.thing.IThingReporter;
import com.github.ompc.athing.thing.ThingException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 傀儡人测试支撑
 */
public class PuppetSupport extends QaTestSupport {

    protected static final Puppet puppet = new Puppet();
    protected static final IThingReporter puppetThingReporter;
    protected static final IThingProduct puppetThingProduct;

    private static final BlockingQueue<ThingEvent> thingEventQueue = new LinkedBlockingQueue<>();

    static {
        try {
            puppetThingReporter = thingPublisher.publish(PRODUCT_ID, THING_ID, puppet);
        } catch (ThingException cause) {
            throw new RuntimeException("publish thing occur error", cause);
        }

        try {
            puppetThingProduct = new ThingProductTemplate(PRODUCT_ID)
                    .hasThingProductProperty("personal_info", PersonalInfo.class)
                    .hasThingProductProperty("contact_info", ContactInfo.class)
                    .hasThingProductProperty("age", Integer.class)
                    .hasThingProductProperty("name", String.class)
                    .hasThingProductService("async_echo", Result.class)
                    .hasThingProductService("echo", Result.class)
                    .hasThingData("tick", Tick.class)
                    .setThingEventListener(thingEventQueue::offer)
                    .makeThingProduct(thingPlatformClientFactory, thingEventConsumerFactory);
        } catch (ThingProductException cause) {
            throw new RuntimeException("make thing-product occur error", cause);
        }

    }

    @SuppressWarnings("unchecked")
    protected static <E extends ThingEvent> E takeThingEventFromQueue() throws InterruptedException {
        return (E) thingEventQueue.take();
    }

}
