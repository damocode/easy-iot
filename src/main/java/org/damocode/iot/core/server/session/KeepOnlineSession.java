package org.damocode.iot.core.server.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

/**
 * @Description: 保持在线会话
 * @Author: zzg
 * @Date: 2021/10/7 14:49
 * @Version: 1.0.0
 */
public class KeepOnlineSession implements DeviceSession, ReplaceableDeviceSession {

    DeviceSession parent;

    @Setter(AccessLevel.PACKAGE)
    private long lastKeepAliveTime = System.currentTimeMillis();

    private final long connectTime = System.currentTimeMillis();

    private long keepAliveTimeOutMs;

    public KeepOnlineSession(DeviceSession parent, Duration keepAliveTimeOut) {
        this.parent = parent;
        setKeepAliveTimeout(keepAliveTimeOut);
    }

    @Override
    public String getId() {
        return parent.getId();
    }

    @Override
    public String getDeviceId() {
        return parent.getDeviceId();
    }

    @Override
    public DeviceOperator getOperator() {
        return parent.getOperator();
    }

    @Override
    public long lastPingTime() {
        return lastKeepAliveTime;
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    public Boolean send(EncodedMessage encodedMessage) {
        if (parent.isAlive()) {
            return parent.send(encodedMessage);
        }
        throw new RuntimeException("connection lost");
    }

    @Override
    public ProtocolSupport getProtocolSupport() {
        return parent.getProtocolSupport();
    }

    @Override
    public Transport getTransport() {
        return parent.getTransport();
    }

    @Override
    public void close() {
        parent.close();
    }

    @Override
    public void ping() {
        lastKeepAliveTime = System.currentTimeMillis();
        parent.keepAlive();
    }

    @Override
    public boolean isAlive() {
        return keepAliveTimeOutMs <= 0
                || System.currentTimeMillis() - lastKeepAliveTime < keepAliveTimeOutMs
                || parent.isAlive();
    }

    @Override
    public void onClose(Runnable call) {
        parent.onClose(call);
    }

    @Override
    public String getServerId() {
        return parent.getServerId();
    }

    @Override
    public Optional<InetSocketAddress> getClientAddress() {
        return parent.getClientAddress();
    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        keepAliveTimeOutMs = timeout.toMillis();
        parent.setKeepAliveTimeout(timeout);
    }

    @Override
    public Duration getKeepAliveTimeout() {
        return Duration.ofMillis(keepAliveTimeOutMs);
    }

    @Override
    public boolean isWrapFrom(Class<?> type) {
        return type == KeepOnlineSession.class || parent.isWrapFrom(type);
    }

    @Override
    public <T extends DeviceSession> T unwrap(Class<T> type) {
        return type == KeepOnlineSession.class ? type.cast(this) : parent.unwrap(type);
    }

    @Override
    public void replaceWith(DeviceSession session) {
        this.parent = session;
    }

}
