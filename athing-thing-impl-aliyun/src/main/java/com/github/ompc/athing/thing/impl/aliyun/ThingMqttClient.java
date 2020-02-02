package com.github.ompc.athing.thing.impl.aliyun;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static com.github.ompc.athing.thing.impl.aliyun.util.StringUtils.bytesToHexString;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 物MQTT客户端
 */
public class ThingMqttClient extends MqttClient {

    // 启动信息
    private final Boot boot;

    /**
     * 构造MQTT客户端
     *
     * @param regionId     MQTT服务器地区编码
     * @param productId    产品ID
     * @param deviceId     设备ID
     * @param deviceSecret 设备密钥
     * @throws MqttException 构建MQTT客户端失败
     */
    public ThingMqttClient(final String regionId,
                           final String productId,
                           final String deviceId,
                           final String deviceSecret) throws MqttException {
        this(new Boot(regionId, productId, deviceId, deviceSecret));
    }

    /**
     * 构造MQTT客户端
     *
     * @param regionId     MQTT服务器地区编码
     * @param productId    产品ID
     * @param deviceId     设备ID
     * @param deviceSecret 设备密钥
     * @param persistence  数据持久化方案
     * @throws MqttException 构建MQTT客户端失败
     */
    public ThingMqttClient(final String regionId,
                           final String productId,
                           final String deviceId,
                           final String deviceSecret,
                           final MqttClientPersistence persistence) throws MqttException {
        this(new Boot(regionId, productId, deviceId, deviceSecret), persistence);
    }

    /**
     * 构造MQTT客户端
     *
     * @param productId       产品ID
     * @param deviceId        设备ID
     * @param deviceSecret    设备密钥
     * @param regionId        MQTT服务器地区编码
     * @param persistence     数据持久化方案
     * @param executorService 工作线程池
     * @throws MqttException 构建MQTT客户端失败
     */
    public ThingMqttClient(final String productId,
                           final String deviceId,
                           final String deviceSecret,
                           final String regionId,
                           final MqttClientPersistence persistence,
                           final ScheduledExecutorService executorService) throws MqttException {
        this(new Boot(regionId, productId, deviceId, deviceSecret), persistence, executorService);
    }

    private ThingMqttClient(final Boot boot,
                            final MqttClientPersistence persistence,
                            final ScheduledExecutorService executorService) throws MqttException {
        super(boot.getServerURI(), boot.getClientId(), persistence, executorService);
        this.boot = boot;
    }

    private ThingMqttClient(final Boot boot, final MqttClientPersistence persistence) throws MqttException {
        super(boot.getServerURI(), boot.getClientId(), persistence);
        this.boot = boot;
    }

    private ThingMqttClient(final Boot boot) throws MqttException {
        super(boot.getServerURI(), boot.getClientId());
        this.boot = boot;
    }

    /**
     * 重定向MQTT日志输出到SLF4J
     */
    public static void loggingRedirectToSLF4J() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * 注入连接参数
     *
     * @param options 连接参数
     * @return options
     */
    private MqttConnectOptions injectMqttConnectOptions(MqttConnectOptions options) {
        options.setUserName(boot.getUsername());
        options.setPassword(boot.getPassword());
        return options;
    }

    @Override
    public void connect() throws MqttException {
        connect(injectMqttConnectOptions(new MqttConnectOptions()));
    }

    @Override
    public void connect(MqttConnectOptions options) throws MqttException {
        super.connect(injectMqttConnectOptions(options));
    }

    @Override
    public IMqttToken connectWithResult(MqttConnectOptions options) throws MqttException {
        return super.connectWithResult(injectMqttConnectOptions(options));
    }

    /**
     * 获取产品ID
     *
     * @return 产品ID
     */
    public String getProductId() {
        return boot.productId;
    }

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    public String getDeviceId() {
        return boot.deviceId;
    }

    /**
     * 启动信息
     */
    private static class Boot {

        final String uniqueId = UUID.randomUUID().toString();
        final long timestamp = System.currentTimeMillis();
        final String regionId;
        final String productId;
        final String deviceId;
        final String deviceSecret;

        /**
         * 构建启动信息
         *
         * @param regionId     MQTT服务器地区编码
         * @param productId    产品ID
         * @param deviceId     设备ID
         * @param deviceSecret 设备密钥
         */
        Boot(String regionId, String productId, String deviceId, String deviceSecret) {
            this.regionId = regionId;
            this.productId = productId;
            this.deviceId = deviceId;
            this.deviceSecret = deviceSecret;
        }

        /**
         * 获取MQTT帐号
         *
         * @return MQTT帐号
         */
        String getUsername() {
            return String.format("%s&%s", deviceId, productId);
        }

        /**
         * 获取MQTT密码
         *
         * @return MQTT密码
         */
        char[] getPassword() {
            final String content = String.format("clientId%sdeviceName%sproductKey%stimestamp%s", uniqueId, deviceId, productId, timestamp);
            try {
                final Mac mac = Mac.getInstance("HMACSHA1");
                mac.init(new SecretKeySpec(deviceSecret.getBytes(UTF_8), mac.getAlgorithm()));
                return bytesToHexString(mac.doFinal(content.getBytes(UTF_8))).toCharArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 获取MQTT服务器地址
         *
         * @return MQTT服务器地址
         */
        String getServerURI() {
            return "tcp://" + productId + ".iot-as-mqtt." + regionId + ".aliyuncs.com:1883";
        }

        /**
         * 获取MQTT客户端ID
         *
         * @return 客户端ID
         */
        String getClientId() {
            return String.format("%s|securemode=3,signmethod=hmacsha1,timestamp=%s,ext=1|", uniqueId, timestamp);
        }

    }

}
