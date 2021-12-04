package org.damocode.iot.network.mqtt.server.vertx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;
import io.vertx.mqtt.messages.MqttUnsubscribeMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.message.codec.MqttMessage;
import org.damocode.iot.core.message.codec.SimpleMqttMessage;
import org.damocode.iot.core.server.mqtt.MqttAuth;
import org.damocode.iot.network.mqtt.server.MqttConnection;
import org.damocode.iot.network.mqtt.server.MqttPublishing;
import org.damocode.iot.network.mqtt.server.MqttSubscription;
import org.damocode.iot.network.mqtt.server.MqttUnSubscription;
import rx.subjects.PublishSubject;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Description: Mqtt连接实现类
 * @Author: zzg
 * @Date: 2021/10/14 9:13
 * @Version: 1.0.0
 */
@Slf4j
public class VertxMqttConnection implements MqttConnection {

    private final MqttEndpoint endpoint;
    private long keepAliveTimeoutMs;
    @Getter
    private long lastPingTime = System.currentTimeMillis();
    private volatile boolean closed = false, accepted = false, autoAckSub = true, autoAckUnSub = true, autoAckMsg = true;

    private final PublishSubject<MqttPublishing> messageProcessor = PublishSubject.create();

    private final PublishSubject<MqttSubscription> subscription = PublishSubject.create();
    private final PublishSubject<MqttUnSubscription> unsubscription =PublishSubject.create();


    private static final MqttAuth emptyAuth = new MqttAuth() {
        @Override
        public String getUsername() {
            return "";
        }

        @Override
        public String getPassword() {
            return "";
        }
    };

    public VertxMqttConnection(MqttEndpoint endpoint) {
        this.endpoint = endpoint;
        this.keepAliveTimeoutMs = (endpoint.keepAliveTimeSeconds() + 10) * 1000L;
    }

    private final Consumer<MqttConnection> defaultListener = mqttConnection -> {
        log.debug("mqtt client [{}] disconnected", getClientId());
        subscription.onCompleted();
        unsubscription.onCompleted();
        messageProcessor.onCompleted();
    };

    private Consumer<MqttConnection> disconnectConsumer = defaultListener;

    @Override
    public String getClientId() {
        return endpoint.clientIdentifier();
    }

    @Override
    public MqttAuth getAuth() {
        return endpoint.auth() == null ? emptyAuth : new VertxMqttAuth();
    }

    @Override
    public void reject(MqttConnectReturnCode code) {
        if (closed) {
            return;
        }
        endpoint.reject(code);
        complete();
    }

    @Override
    public MqttConnection accept() {
        if (accepted) {
            return this;
        }
        log.debug("mqtt client [{}] connected", getClientId());
        accepted = true;
        try {
            if (!endpoint.isConnected()) {
                endpoint.accept();
            }
        } catch (Exception e) {
            close();
            log.warn(e.getMessage(), e);
            return this;
        }
        init();
        return this;
    }

    @Override
    public Optional<MqttMessage> getWillMessage() {
        return Optional.ofNullable(endpoint.will())
                .filter(will -> will.getWillMessageBytes() != null)
                .map(will -> SimpleMqttMessage.builder()
                        .will(true)
                        .payload(Unpooled.wrappedBuffer(will.getWillMessageBytes()))
                        .topic(will.getWillTopic())
                        .qosLevel(will.getWillQos())
                        .build());
    }

    @Override
    public PublishSubject<MqttPublishing> handleMessage() {
        if(messageProcessor.hasCompleted()){
            return null;
        }
        return messageProcessor;
    }

    @Override
    public Boolean publish(MqttMessage message) {
        ping();
        Buffer buffer = Buffer.buffer(message.getPayload());
        AtomicBoolean flag = new AtomicBoolean(false);
        endpoint.publish(message.getTopic(),
            buffer,
            MqttQoS.valueOf(message.getQosLevel()),
            message.isDup(),
            message.isRetain(),
            result -> {
                if (result.succeeded()) {
                    flag.set(true);
                } else {
                    flag.set(false);
                }
            }
        );
        return flag.get();
    }

    @Override
    public PublishSubject<MqttSubscription> handleSubscribe(boolean autoAck) {
        autoAckSub = autoAck;
        return subscription;
    }

    @Override
    public PublishSubject<MqttUnSubscription> handleUnSubscribe(boolean autoAck) {
        autoAckUnSub = autoAck;
        return unsubscription;
    }

    @Override
    public void onClose(Consumer<MqttConnection> listener) {
        disconnectConsumer = disconnectConsumer.andThen(listener);
    }

    @Override
    public boolean isAlive() {
        return endpoint.isConnected() && (keepAliveTimeoutMs < 0 || ((System.currentTimeMillis() - lastPingTime) < keepAliveTimeoutMs));
    }

    @Override
    public void close() {
        if (endpoint.isConnected()) {
            endpoint.close();
        }
        this.complete();
    }

    @Override
    public void keepAlive() {
        ping();
    }

    @Override
    public void setKeepAliveTimeout(Duration duration) {
        keepAliveTimeoutMs = duration.toMillis();
    }

    private volatile InetSocketAddress clientAddress;

    @Override
    public InetSocketAddress getClientAddress() {
        if (clientAddress == null) {
            SocketAddress address = endpoint.remoteAddress();
            if (address != null) {
                clientAddress = new InetSocketAddress(address.host(), address.port());
            }
        }
        return clientAddress;
    }

    void ping() {
        lastPingTime = System.currentTimeMillis();
    }

    void init() {
        this.endpoint
            .disconnectHandler(ignore -> this.complete())
            .closeHandler(ignore -> this.complete())
            .pingHandler(ignore -> {
                this.ping();
                if (!endpoint.isAutoKeepAlive()) {
                    endpoint.pong();
                }
            })
            .publishHandler(msg -> {
                ping();
                VertxMqttPublishing publishing = new VertxMqttPublishing(msg, false);
                boolean hasDownstream = this.messageProcessor.hasObservers();
                if (autoAckMsg || !hasDownstream) {
                    publishing.acknowledge();
                }
                if (hasDownstream) {
                    this.messageProcessor.onNext(publishing);
                }
            })
            //QoS 1 PUBACK
            .publishAcknowledgeHandler(messageId -> {
                ping();
                log.debug("PUBACK mqtt[{}] message[{}]", getClientId(), messageId);
            })
            //QoS 2  PUBREC
            .publishReceivedHandler(messageId -> {
                ping();
                log.debug("PUBREC mqtt[{}] message[{}]", getClientId(), messageId);
                endpoint.publishRelease(messageId);
            })
            //QoS 2  PUBREL
            .publishReleaseHandler(messageId -> {
                ping();
                log.debug("PUBREL mqtt[{}] message[{}]", getClientId(), messageId);
                endpoint.publishComplete(messageId);
            })
            //QoS 2  PUBCOMP
            .publishCompletionHandler(messageId -> {
                ping();
                log.debug("PUBCOMP mqtt[{}] message[{}]", getClientId(), messageId);
            })
            .subscribeHandler(msg -> {
                ping();
                VertxMqttSubscription subscription = new VertxMqttSubscription(msg, false);
                boolean hasDownstream = this.subscription.hasObservers();
                if (autoAckSub || !hasDownstream) {
                    subscription.acknowledge();
                }
                if (hasDownstream) {
                    this.subscription.onNext(subscription);
                }
            })
            .unsubscribeHandler(msg -> {
                ping();
                VertxMqttMqttUnSubscription unSubscription = new VertxMqttMqttUnSubscription(msg, false);
                boolean hasDownstream = this.unsubscription.hasObservers();
                if (autoAckUnSub || !hasDownstream) {
                    unSubscription.acknowledge();
                }
                if (hasDownstream) {
                    this.unsubscription.onNext(unSubscription);
                }
            });
    }

    private void complete() {
        if (closed) {
            return;
        }
        closed = true;
        disconnectConsumer.accept(this);
        disconnectConsumer = defaultListener;
    }

    @AllArgsConstructor
    class VertxMqttMessage implements MqttMessage {
        MqttPublishMessage message;

        @Nonnull
        @Override
        public String getTopic() {
            return message.topicName();
        }

        @Override
        public String getClientId() {
            return VertxMqttConnection.this.getClientId();
        }

        @Override
        public int getMessageId() {
            return message.messageId();
        }

        @Override
        public boolean isWill() {
            return false;
        }

        @Override
        public int getQosLevel() {
            return message.qosLevel().value();
        }

        @Override
        public boolean isDup() {
            return message.isDup();
        }

        @Override
        public boolean isRetain() {
            return message.isRetain();
        }

        @Nonnull
        @Override
        public ByteBuf getPayload() {
            return message.payload().getByteBuf();
        }

        @Override
        public String toString() {
            return print();
        }
    }

    @AllArgsConstructor
    class VertxMqttPublishing implements MqttPublishing {

        private final MqttPublishMessage message;

        private volatile boolean acknowledged;

        @Override
        public MqttMessage getMessage() {
            return new VertxMqttMessage(message);
        }

        @Override
        public void acknowledge() {
            if (acknowledged) {
                return;
            }
            acknowledged = true;
            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                log.debug("PUBACK QoS1 mqtt[{}] message[{}]", getClientId(), message.messageId());
                endpoint.publishAcknowledge(message.messageId());
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                log.debug("PUBREC QoS2 mqtt[{}] message[{}]", getClientId(), message.messageId());
                endpoint.publishReceived(message.messageId());
            }
        }
    }

    @AllArgsConstructor
    class VertxMqttSubscription implements MqttSubscription {

        private final MqttSubscribeMessage message;

        private volatile boolean acknowledged;

        @Override
        public MqttSubscribeMessage getMessage() {
            return message;
        }

        @Override
        public synchronized void acknowledge() {
            if (acknowledged) {
                return;
            }
            acknowledged = true;
            endpoint.subscribeAcknowledge(message.messageId(), message.topicSubscriptions().stream()
                    .map(MqttTopicSubscription::qualityOfService).collect(Collectors.toList()));
        }
    }

    @AllArgsConstructor
    class VertxMqttMqttUnSubscription implements MqttUnSubscription {

        private final MqttUnsubscribeMessage message;

        private volatile boolean acknowledged;

        @Override
        public MqttUnsubscribeMessage getMessage() {
            return message;
        }

        @Override
        public synchronized void acknowledge() {
            if (acknowledged) {
                return;
            }
            log.info("acknowledge mqtt [{}] unsubscribe : {} ", getClientId(), message.topics());
            acknowledged = true;
            endpoint.unsubscribeAcknowledge(message.messageId());
        }
    }

    class VertxMqttAuth implements MqttAuth {

        @Override
        public String getUsername() {
            return endpoint.auth().getUsername();
        }

        @Override
        public String getPassword() {
            return endpoint.auth().getPassword();
        }
    }
}
