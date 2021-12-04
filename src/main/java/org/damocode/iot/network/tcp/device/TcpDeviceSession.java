package org.damocode.iot.network.tcp.device;

import lombok.Getter;
import lombok.Setter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.network.tcp.TcpMessage;
import org.damocode.iot.network.tcp.client.TcpClient;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description: Tcp设备会话
 * @Author: zzg
 * @Date: 2021/10/7 15:04
 * @Version: 1.0.0
 */
public class TcpDeviceSession implements DeviceSession {

    @Getter
    @Setter
    private DeviceOperator operator;

    @Setter
    private TcpClient client;

    @Getter
    private final ProtocolSupport protocolSupport;

    @Getter
    private final Transport transport;

    private long lastPingTime = System.currentTimeMillis();

    private final long connectTime = System.currentTimeMillis();

    TcpDeviceSession(DeviceOperator operator,TcpClient client,ProtocolSupport protocolSupport,Transport transport) {
        this.operator = operator;
        this.client = client;
        this.protocolSupport = protocolSupport;
        this.transport = transport;
    }

    @Override
    public String getId() {
        return getDeviceId();
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
    public void setKeepAliveTimeout(Duration timeout) {
        client.setKeepAliveTimeout(timeout);
    }

    @Override
    public boolean isAlive() {
        return client.isAlive();
    }

    @Override
    public Optional<InetSocketAddress> getClientAddress() {
        return Optional.ofNullable(client.getRemoteAddress());
    }

    @Override
    public void onClose(Runnable call) {
        client.onDisconnect(call);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TcpDeviceSession session = (TcpDeviceSession) o;
        return Objects.equals(client, session.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client);
    }

}
