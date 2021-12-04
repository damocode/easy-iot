package org.damocode.iot.network.tcp.device;

import lombok.Getter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.network.tcp.TcpMessage;
import org.damocode.iot.network.tcp.client.TcpClient;

/**
 * @Description: 未知的Tcp设备会话
 * @Author: zzg
 * @Date: 2021/10/7 15:04
 * @Version: 1.0.0
 */
class UnknownTcpDeviceSession implements DeviceSession {

    @Getter
    private final String id;

    private final TcpClient client;

    @Getter
    private final ProtocolSupport protocolSupport;

    @Getter
    private final Transport transport;

    private long lastPingTime = System.currentTimeMillis();

    private final long connectTime = System.currentTimeMillis();

    UnknownTcpDeviceSession(String id,TcpClient client,ProtocolSupport protocolSupport,Transport transport) {
        this.id = id;
        this.client = client;
        this.protocolSupport = protocolSupport;
        this.transport = transport;
    }

    @Override
    public String getDeviceId() {
        return "unknown";
    }

    @Override
    public DeviceOperator getOperator() {
        return null;
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
        return client.send(new TcpMessage(encodedMessage.getPayload()));
    }

    @Override
    public void close() {
        client.shutdown();
    }

    @Override
    public void ping() {
        lastPingTime = System.currentTimeMillis();
        client.keepAlive();
    }

    @Override
    public boolean isAlive() {
        return client.isAlive();
    }

    @Override
    public void onClose(Runnable call) {
        client.onDisconnect(call);
    }

}
