package org.damocode.iot.network.http.gateway;

import lombok.Getter;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DefaultTransport;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.session.DeviceSession;

/**
 * @Description: 未知的Http设备会话
 * @Author: zzg
 * @Date: 2021/10/26 11:39
 * @Version: 1.0.0
 */
class UnknownHttpDeviceSession implements DeviceSession {

    @Getter
    private final ProtocolSupport protocolSupport;

    UnknownHttpDeviceSession(ProtocolSupport protocolSupport) {
        this.protocolSupport = protocolSupport;
    }

    @Override
    public String getId() {
        return "unknown";
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
        return 0;
    }

    @Override
    public long connectTime() {
        return 0;
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
    public void close() {

    }

    @Override
    public void ping() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void onClose(Runnable call) {

    }

}
