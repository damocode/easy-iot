package org.damocode.iot.network.mqtt.device.session;

import lombok.Getter;
import lombok.Setter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.*;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.network.mqtt.client.MqttClient;

import java.time.Duration;

/**
 * @Description: Mqtt客户端会话
 * @Author: zzg
 * @Date: 2021/10/12 10:55
 * @Version: 1.0.0
 */
public class MqttClientSession implements DeviceSession {

    @Getter
    private final String id;

    @Getter
    private final DeviceOperator operator;

    @Getter
    @Setter
    private final MqttClient client;

    @Getter
    private final ProtocolSupport protocolSupport;

    @Getter
    private final Transport transport;

    private final long connectTime = System.currentTimeMillis();

    private long lastPingTime = System.currentTimeMillis();

    private long keepAliveTimeout = -1;

    public MqttClientSession(String id,
                             DeviceOperator operator,
                             MqttClient client,
                             ProtocolSupport protocolSupport,
                             Transport transport) {
        this.id = id;
        this.operator = operator;
        this.client = client;
        this.protocolSupport = protocolSupport;
        this.transport = transport;
    }


    @Override
    public String getDeviceId() {
        return operator.getDeviceId();
    }

    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    @Override
    public Boolean send(EncodedMessage encodedMessage) {
        if (encodedMessage instanceof MqttMessage) {
            client.publish(((MqttMessage) encodedMessage));
            return true;
        }
        throw new UnsupportedOperationException("unsupported message type:" + encodedMessage.getClass());
    }

    @Override
    public void close() {

    }

    @Override
    public void ping() {
        lastPingTime = System.currentTimeMillis();
    }

    @Override
    public boolean isAlive() {
        return client.isAlive() &&
                (keepAliveTimeout <= 0 || System.currentTimeMillis() - lastPingTime < keepAliveTimeout);
    }

    @Override
    public void onClose(Runnable call) {

    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        this.keepAliveTimeout = timeout.toMillis();
    }

    @Override
    public ProtocolSupport getProtocolSupport() {
        return null;
    }

    @Override
    public String toString() {
        return "MqttClientSession{" +
                "id=" + id + ",device=" + getDeviceId() +
                '}';
    }
}

