package org.damocode.iot.network.mqtt.device.session;

import lombok.Getter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.*;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.network.mqtt.client.MqttClient;

/**
 * @Description: 未知设备Mqtt客户端会话
 * @Author: zzg
 * @Date: 2021/10/12 10:57
 * @Version: 1.0.0
 */
public class UnknownDeviceMqttClientSession implements DeviceSession {

    @Getter
    private String id;

    private MqttClient client;

    @Getter
    private final ProtocolSupport protocolSupport;

    @Getter
    private final Transport transport;


    public UnknownDeviceMqttClientSession(String id, MqttClient client,ProtocolSupport protocolSupport,Transport transport) {
        this.id = id;
        this.client = client;
        this.protocolSupport = protocolSupport;
        this.transport = transport;
    }

    @Override
    public String getDeviceId() {
        return null;
    }

    @Override
    public DeviceOperator getOperator() {
        return null;
    }

    @Override
    public long lastPingTime() {
        return 0;
    }

    @Override
    public long connectTime() {
        return 0;
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

    }

    @Override
    public boolean isAlive() {
        return client.isAlive();
    }

    @Override
    public void onClose(Runnable call) {

    }

    @Override
    public ProtocolSupport getProtocolSupport() {
        return null;
    }

}

