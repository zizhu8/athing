package com.github.ompc.athing.product.impl.aliyun;

import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.util.Base64;
import java.util.Hashtable;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * ThingJmsClient
 */
public class ThingJmsClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Boot boot;

    public ThingJmsClient(final String aliyunUID,
                          final String regionId,
                          final String accessKeyId,
                          final String accessKeySecret,
                          final String consumerGroup) {
        this.boot = new Boot(
                aliyunUID,
                regionId,
                accessKeyId,
                accessKeySecret,
                consumerGroup
        );
    }

    // 初始化JMS
    private Context initJmsContext(String serverURI) throws NamingException {
        final Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("connectionfactory.SBCF", serverURI);
        hashtable.put("queue.QUEUE", boot.getConsumerGroup());
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        return new InitialContext(hashtable);
    }

    // 建立JMS连接
    private Connection initJmsConnection(Context jmsContext) throws NamingException, JMSException {
        final ConnectionFactory jmsConnectionFactory = (ConnectionFactory) jmsContext.lookup("SBCF");
        final Connection connection = jmsConnectionFactory.createConnection(boot.getUsername(), boot.getPassword());
        if (connection instanceof JmsConnection) {
            ((JmsConnection) connection).addConnectionListener(new LoggingJmsConnectionListener());
        }
        return connection;
    }

    /**
     * 连接Jms服务器
     *
     * @return Jms消息消费者
     * @throws NamingException NamingException
     * @throws JMSException    JMSException
     */
    public MessageConsumer connect() throws JMSException, NamingException {
        return connect(boot.getConnectionURI());
    }

    /**
     * 连接Jms服务器
     *
     * @param serverURI 服务器地址
     * @return Jms消息消费者
     * @throws NamingException NamingException
     * @throws JMSException    JMSException
     */
    public MessageConsumer connect(String serverURI) throws NamingException, JMSException {
        final Context context = initJmsContext(serverURI);
        final Connection connection = initJmsConnection(context);
        final Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        connection.start();
        final Destination queue = (Destination) context.lookup("QUEUE");
        return session.createConsumer(queue);
    }

    /**
     * 引导信息
     */
    private static class Boot {
        final String uniqueId = UUID.randomUUID().toString();
        final long timestamp = System.currentTimeMillis();
        final String aliyunUID;
        final String regionId;
        final String accessKeyId;
        final String accessKeySecret;
        final String consumerGroup;

        private Boot(final String aliyunUID,
                     final String regionId,
                     final String accessKeyId,
                     final String accessKeySecret,
                     final String consumerGroup) {
            this.aliyunUID = aliyunUID;
            this.regionId = regionId;
            this.accessKeyId = accessKeyId;
            this.accessKeySecret = accessKeySecret;
            this.consumerGroup = consumerGroup;
        }

        String getUsername() {
            return String.format("%s|authMode=aksign,signMethod=hmacsha1,timestamp=%s,authId=%s,consumerGroupId=%s|",
                    uniqueId,
                    timestamp,
                    accessKeyId,
                    consumerGroup
            );
        }

        String getPassword() {
            final String content = String.format("authId=%s&timestamp=%s", accessKeyId, timestamp);
            try {
                final Mac mac = Mac.getInstance("HMACSHA1");
                mac.init(new SecretKeySpec(accessKeySecret.getBytes(UTF_8), mac.getAlgorithm()));
                return Base64.getEncoder().encodeToString(mac.doFinal(content.getBytes(UTF_8)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String getConnectionURI() {
            return String.format(
                    "failover:(amqps://%s.iot-amqp.%s.aliyuncs.com:5671?amqp.idleTimeout=80000)?failover.reconnectDelay=30",
                    aliyunUID, regionId
            );
        }

        String getConsumerGroup() {
            return consumerGroup;
        }

    }

    /**
     * JMS连接状态记录
     */
    class LoggingJmsConnectionListener implements JmsConnectionListener {

        @Override
        public void onConnectionEstablished(URI uri) {
            logger.info("jms connection success. server={}", uri);
        }

        @Override
        public void onConnectionFailure(Throwable cause) {
            logger.warn("jms connection failed.", cause);
        }

        @Override
        public void onConnectionInterrupted(URI uri) {
            logger.warn("jms connection interrupted. server={}", uri);
        }

        @Override
        public void onConnectionRestored(URI uri) {
            logger.warn("jms connection restored. server={}", uri);
        }

        @Override
        public void onInboundMessage(JmsInboundMessageDispatch jmsInboundMessageDispatch) {

        }

        @Override
        public void onSessionClosed(Session session, Throwable cause) {
            logger.warn("jms session closed, session={}", session, cause);
        }

        @Override
        public void onConsumerClosed(MessageConsumer messageConsumer, Throwable cause) {
            logger.warn("jms consumer closed, consumer={}", messageConsumer, cause);
        }

        @Override
        public void onProducerClosed(MessageProducer messageProducer, Throwable cause) {
            logger.warn("jms producer closed, producer={}", messageProducer, cause);
        }
    }


}
