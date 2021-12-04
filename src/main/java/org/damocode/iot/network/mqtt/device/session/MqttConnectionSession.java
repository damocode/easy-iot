package org.damocode.iot.network.mqtt.device.session;

import lombok.Getter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.MqttMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.core.server.session.ReplaceableDeviceSession;
import org.damocode.iot.network.mqtt.server.MqttConnection;

/**
 * @Description: Mqtt连接会话
 * @Author: zzg
 * @Date: 2021/10/15 11:10
 * @Version: 1.0.0
 */
public class MqttConnectionSession implements DeviceSession, ReplaceableDeviceSession {

    @Getter
    private final String id;

    @Getter
    private final DeviceOperator operator;

    @Getter
    private final ProtocolSupport protocolSupport;

    @Getter
    private final Transport transport;

    @Getter
    private MqttConnection connection;

    private final long connectTime = System.currentTimeMillis();

    public MqttConnectionSession(String id,
                                 DeviceOperator operator,
                                 ProtocolSupport protocolSupport,
                                 Transport transport,
                                 MqttConnection connection) {
        this.id = id;
        this.operator = operator;
        this.protocolSupport = protocolSupport;
        this.transport = transport;
        this.connection = connection;
    }

    @Override
    public String getDeviceId() {
        return getId();
    }

    @Override
    public long lastPingTime() {
        return connection.getLastPingTime();
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    @Override
    public Boolean send(EncodedMessage encodedMessage) {
        return connection.publish(((MqttMessage) encodedMessage));
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public void ping() {
        connection.keepAlive();
    }

    @Override
    public boolean isAlive() {
        return connection.isAlive();
    }

    @Override
    public void onClose(Runnable call) {
        connection.onClose(c -> call.run());
    }

    @Override
    public void replaceWith(DeviceSession session) {
        if (session instanceof MqttConnectionSession) {
            MqttConnectionSession connectionSession = ((MqttConnectionSession) session);
            this.connection = connectionSession.connection;
        }
    }
}
