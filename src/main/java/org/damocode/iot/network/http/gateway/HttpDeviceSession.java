package org.damocode.iot.network.http.gateway;

import lombok.Getter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DefaultTransport;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

/**
 * @Description: Http设备会话
 * @Author: zzg
 * @Date: 2021/10/26 11:34
 * @Version: 1.0.0
 */
class HttpDeviceSession implements DeviceSession {

    private final DeviceOperator operator;

    private final InetSocketAddress address;

    @Getter
    private final ProtocolSupport protocolSupport;

    private long lastPingTime = System.currentTimeMillis();

    private long keepAliveTimeOutMs = -1;

    public HttpDeviceSession(DeviceOperator deviceOperator, InetSocketAddress address,ProtocolSupport protocolSupport) {
        this.operator = deviceOperator;
        this.protocolSupport = protocolSupport;
        this.address = address;
    }

    @Override
    public String getId() {
        return operator.getDeviceId();
    }

    @Override
    public String getDeviceId() {
        return operator.getDeviceId();
    }

    @Override
    public DeviceOperator getOperator() {
        return operator;
    }

    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    @Override
    public long connectTime() {
        return lastPingTime;
    }

    @Override
    public Boolean send(EncodedMessage encodedMessage) {
        return false;
    }

    @Override
    public Transport getTransport() {
        return DefaultTransport.HTTP;
    }

    @Override
    public Optional<InetSocketAddress> getClientAddress() {
        return Optional.ofNullable(address);
    }

    @Override
    public void close() {

    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        keepAliveTimeOutMs = timeout.toMillis();
    }

    @Override
    public void ping() {
        lastPingTime = System.currentTimeMillis();
    }

    @Override
    public boolean isAlive() {
        return keepAliveTimeOutMs <= 0
                || System.currentTimeMillis() - lastPingTime < keepAliveTimeOutMs;
    }

    @Override
    public void onClose(Runnable call) {

    }

}
