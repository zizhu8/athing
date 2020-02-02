package com.github.ompc.athing.qatest;

import com.github.ompc.athing.product.IFactory;
import com.github.ompc.athing.product.IThingEventConsumer;
import com.github.ompc.athing.product.IThingPlatformClient;
import com.github.ompc.athing.product.impl.aliyun.ThingAcsClient;
import com.github.ompc.athing.product.impl.aliyun.ThingEventConsumerFactory;
import com.github.ompc.athing.product.impl.aliyun.ThingJmsClient;
import com.github.ompc.athing.product.impl.aliyun.ThingPlatformClientFactory;
import com.github.ompc.athing.thing.IThingPublisher;
import com.github.ompc.athing.thing.impl.aliyun.ThingMqttClient;
import com.github.ompc.athing.thing.impl.aliyun.ThingPublisherImpl;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;

public class QaTestSupport {

    protected static final Properties properties;
    protected static final String PRODUCT_ID;
    protected static final String THING_ID;

    protected static final IFactory<IThingPlatformClient> thingPlatformClientFactory;
    protected static final IFactory<IThingEventConsumer> thingEventConsumerFactory;
    protected static final IThingPublisher thingPublisher;

    static {

        // 加载配置文件
        properties = initProperties(new Properties());

        // 常量信息
        PRODUCT_ID = properties.getProperty("puppet.thing.product");
        THING_ID = properties.getProperty("puppet.thing.id");

        // 初始化平台客户端工厂
        thingPlatformClientFactory = initThingPlatformClientFactory();

        // 初始化平台消息工厂
        thingEventConsumerFactory = initThingEventConsumerFactory();

        // 初始化设备发布器
        thingPublisher = initThingPublisher();
    }


    /**
     * 初始化配置文件
     *
     * @param properties 配置信息
     * @return 配置信息
     */
    private static Properties initProperties(Properties properties) {
        // 读取配置文件
        final String propertiesFilePath = System.getProperties().getProperty("athing-qatest.properties.file");
        final File propertiesFile = new File(propertiesFilePath);
        if (!propertiesFile.exists() || !propertiesFile.canRead()) {
            throw new RuntimeException(String.format("loading properties error: file not existed: %s", propertiesFilePath));
        }
        try (final InputStream is = new FileInputStream(propertiesFile)) {
            properties.load(is);
            return properties;
        } catch (Exception cause) {
            throw new RuntimeException("loading properties error!", cause);
        }
    }

    private static IFactory<IThingPlatformClient> initThingPlatformClientFactory() {
        return new ThingPlatformClientFactory(
                new ThingAcsClient(
                        properties.getProperty("puppet.product.region"),
                        properties.getProperty("puppet.product.acs.access-key-id"),
                        properties.getProperty("puppet.product.acs.access-key-secret")
                )
        );
    }

    private static IFactory<IThingEventConsumer> initThingEventConsumerFactory() {
        return new ThingEventConsumerFactory(
                new ThingJmsClient(
                        properties.getProperty("puppet.product.jms.aliyun-uid"),
                        properties.getProperty("puppet.product.region"),
                        properties.getProperty("puppet.product.jms.access-key-id"),
                        properties.getProperty("puppet.product.jms.access-key-secret"),
                        properties.getProperty("puppet.product.jms.group")
                )
        );
    }

    private static IThingPublisher initThingPublisher() {
        final IMqttClient mqttClient;
        try {
            // ThingMqttClient.loggingRedirectToSLF4J();
            mqttClient = new ThingMqttClient(
                    properties.getProperty("puppet.thing.region"),
                    properties.getProperty("puppet.thing.product"),
                    properties.getProperty("puppet.thing.id"),
                    properties.getProperty("puppet.thing.secret"),
                    new MemoryPersistence()
            );
            final MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MQTT_VERSION_3_1_1);
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            mqttClient.connect(options);
        } catch (MqttException e) {
            throw new RuntimeException("init thing-provider error", e);
        }
        return new ThingPublisherImpl(
                mqttClient,
                newFixedThreadPool(parseInt(properties.getProperty("puppet.thing.worker-num")))
        );
    }

}
