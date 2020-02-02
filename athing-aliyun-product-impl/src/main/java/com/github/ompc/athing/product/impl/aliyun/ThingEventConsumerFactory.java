package com.github.ompc.athing.product.impl.aliyun;

import com.github.ompc.athing.product.IFactory;
import com.github.ompc.athing.product.IThingEventConsumer;
import com.github.ompc.athing.product.ThingProductException;
import com.github.ompc.athing.product.definition.ThingProductDefinition;
import com.github.ompc.athing.product.event.IThingEventListener;
import com.github.ompc.athing.product.impl.aliyun.message.ThingMessageListener;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

public class ThingEventConsumerFactory implements IFactory<IThingEventConsumer> {

    private final ThingJmsClient jmsClient;

    public ThingEventConsumerFactory(ThingJmsClient jmsClient) {
        this.jmsClient = jmsClient;
    }

    @Override
    public IThingEventConsumer make(ThingProductDefinition definition) throws ThingProductException {
        try {
            final MessageConsumer jmsMessageConsumer = jmsClient.connect();
            return new IThingEventConsumer() {

                @Override
                public void setThingEventListener(IThingEventListener listener) throws ThingProductException {
                    try {
                        jmsMessageConsumer.setMessageListener(new ThingMessageListener(definition, listener));
                    } catch (JMSException cause) {
                        throw new ThingProductException(definition.getProductId(), "set thing-event-listener occur error!", cause);
                    }
                }

                @Override
                public void close() throws Exception {
                    jmsMessageConsumer.close();
                }
            };
        } catch (Exception cause) {
            throw new ThingProductException(definition.getProductId(), "making thing-event consumer occur error!", cause);
        }
    }

}
