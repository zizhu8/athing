package com.github.ompc.athing.thing.impl.aliyun;

import com.github.ompc.athing.thing.IThing;
import com.github.ompc.athing.thing.IThingPublisher;
import com.github.ompc.athing.thing.IThingReporter;
import com.github.ompc.athing.thing.ThingException;
import com.github.ompc.athing.thing.impl.aliyun.message.ThingMessageProcessor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ThingPublisherImpl implements IThingPublisher {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IMqttClient client;
    private final ExecutorService workers;

    public ThingPublisherImpl(IMqttClient client, ExecutorService workers) {
        this.client = client;
        this.workers = workers;
    }

    @Override
    public IThingReporter publish(IThing thing) throws ThingException {

        final ThingMessageProcessor processor = new ThingMessageProcessor(thing, client, workers);
        logger.info("thing-publish:/{}/{} publish finished.", thing.getProductId(), thing.getThingId());

        return new IThingReporter() {

            @Override
            public void reportThingProperty(String... propertyIds) throws ThingException {
                processor.reportThingProperties(propertyIds);
            }

            @Override
            public void reportThingData(String dataId, Object data) throws ThingException {
                processor.reportThingData(dataId, data);
            }

        };
    }

}
