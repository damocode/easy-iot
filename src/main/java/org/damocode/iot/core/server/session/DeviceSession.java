package org.damocode.iot.core.server.session;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

/**
 * @Description: 设备会话
 * @Author: zzg
 * @Date: 2021/10/7 14:40
 * @Version: 1.0.0
 */
public interface DeviceSession {

    String getId();

    String getDeviceId();

    /**
     * 获取设备操作对象,在类似TCP首次请求的场景下,返回值可能为null. 可以通过判断此返回值是否为null,
     * 来处理首次连接的情况
     * @return
     */
    DeviceOperator getOperator();

    long lastPingTime();

    long connectTime();

    Boolean send(EncodedMessage encodedMessage);

    void close();

    void ping();

    boolean isAlive();

    void onClose(Runnable call);

    default String getServerId() {
        return null;
    }

    default Optional<InetSocketAddress> getClientAddress() {
        return Optional.empty();
    }

    default void keepAlive() {
        ping();
    }

    default void setKeepAliveTimeout(Duration timeout) {
    }

    default Duration getKeepAliveTimeout(){
        return Duration.ZERO;
    }

    DeviceMessageCodec getDeviceMessageCodec();

    default boolean isWrapFrom(Class<?> type) {
        return type.isInstance(this);
    }

    default <T extends DeviceSession> T unwrap(Class<T> type) {
        return type.cast(this);
    }

}
