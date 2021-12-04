package org.damocode.iot.network.mqtt.device;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.message.codec.*;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.session.DeviceSessionManager;
import org.damocode.iot.network.mqtt.client.MqttClient;
import org.damocode.iot.network.mqtt.device.session.UnknownDeviceMqttClientSession;
import org.damocode.iot.network.utils.DeviceGatewayHelper;
import org.damocode.iot.network.mqtt.device.session.MqttClientSession;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description: Mqtt客户端设备网关
 * @Author: zzg
 * @Date: 2021/10/12 10:03
 * @Version: 1.0.0
 */
@Slf4j
public class MqttClientDeviceGateway {

    @Getter
    private final String id;

    private final MqttClient mqttClient;

    private ProtocolSupport protocolSupport;

    private final DeviceGatewayHelper helper;

    private final List<String> topics;

    private final AtomicBoolean started = new AtomicBoolean();

    private PublishSubject<Message> subject = PublishSubject.create();

    private List<PublishSubject<MqttMessage>> pubs = new CopyOnWriteArrayList<>();

    public MqttClientDeviceGateway(String id,
                                   MqttClient mqttClient,
                                   DeviceOperatorManager deviceOperatorManager,
                                   DeviceSessionManager sessionManager,
                                   ProtocolSupport protocolSupport,
                                   DecodedClientMessageHandler clientMessageHandler,
                                   List<String> topics) {
        this.id = Objects.requireNonNull(id, "id");
        this.mqttClient = Objects.requireNonNull(mqttClient, "mqttClient");
        this.topics = Objects.requireNonNull(topics, "topics");
        this.protocolSupport = Objects.requireNonNull(protocolSupport,"protocolSupport");
        this.helper = new DeviceGatewayHelper(deviceOperatorManager,sessionManager,clientMessageHandler);
    }

    private void doStart() {
        if (started.getAndSet(true)) {
            return;
        }
        PublishSubject<MqttMessage> pub = mqttClient.subscribe(topics,mqttMessage -> {
            AtomicReference<Duration> timeoutRef = new AtomicReference<>();
            DeviceMessageCodec codec = protocolSupport.getMessageCodec(getTransport());
            DeviceMessage deviceMessage = codec.decode(FromDeviceMessageContext.of(
                    new UnknownDeviceMqttClientSession(id + ":unknown", mqttClient,protocolSupport,getTransport()) {
                        @Override
                        public Boolean send(EncodedMessage encodedMessage) {
                            Boolean flag = super.send(encodedMessage);
                            return flag;
                        }

                        @Override
                        public void setKeepAliveTimeout(Duration timeout) {
                            timeoutRef.set(timeout);
                        }
                    }
                    , mqttMessage)
            );
            if(deviceMessage == null){
                return;
            }
            if (subject.hasObservers()) {
                subject.onNext(deviceMessage);
            }
            helper.handleDeviceMessage(deviceMessage, device -> createDeviceSession(device, mqttClient), DeviceGatewayHelper.applySessionKeepaliveTimeout(deviceMessage, timeoutRef::get));
        });
        pubs.add(pub);
    }

    public Transport getTransport() {
        return DefaultTransport.MQTT;
    }

    private MqttClientSession createDeviceSession(DeviceOperator device, MqttClient client) {
        return new MqttClientSession(device.getDeviceId(), device, client,protocolSupport,getTransport());
    }

    public void startup() {
        this.doStart();
    }

    public PublishSubject<Message> onMessage() {
        return subject;
    }

    public void shutdown() {
        started.set(false);
        pubs.forEach(PublishSubject::onCompleted);
        pubs.clear();
    }
}
