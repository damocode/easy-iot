package org.damocode.iot.core.server.session;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;

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

    /**
     * @return 会话ID
     */
    String getId();

    /**
     * @return 设备ID
     */
    String getDeviceId();

    /**
     * 获取设备操作对象,在类似TCP首次请求的场景下,返回值可能为null. 可以通过判断此返回值是否为null,
     * 来处理首次连接的情况
     * @return
     */
    DeviceOperator getOperator();

    /**
     * @return 最近心跳时间
     */
    long lastPingTime();

    /**
     * @return 创建时间
     */
    long connectTime();

    /**
     * 发送消息给会话
     * @param encodedMessage 消息
     * @return 是否成功
     */
    Boolean send(EncodedMessage encodedMessage);

    /**
     * 关闭session
     */
    void close();

    /**
     * 心跳
     */
    void ping();

    /**
     * @return 会话是否存活
     */
    boolean isAlive();

    /**
     * 设置close回调
     * @param call 回调
     */
    void onClose(Runnable call);

    /**
     * @return 会话连接的服务ID
     */
    default String getServerId() {
        return null;
    }

    /**
     * @return 客户端地址
     */
    default Optional<InetSocketAddress> getClientAddress() {
        return Optional.empty();
    }

    default void keepAlive() {
        ping();
    }

    /**
     * 设置心跳超时时间
     * @param timeout 心跳超时时间
     */
    default void setKeepAliveTimeout(Duration timeout) {
    }

    default Duration getKeepAliveTimeout(){
        return Duration.ZERO;
    }

    /**
     * @return 消息协议支持接口
     */
    ProtocolSupport getProtocolSupport();

    /**
     * @return 传输协议,比如MQTT,TCP等
     */
    Transport getTransport();

    /**
     * @return 设备消息转换器
     */
    default DeviceMessageCodec getDeviceMessageCodec() {
        return this.getProtocolSupport().getMessageCodec(getTransport());
    }

    /**
     * 判断会话是否包装自指定的类型,在某些场景下,可能会对会话进行包装
     * @param type 类型
     * @return 是否包装自指定的类型
     */
    default boolean isWrapFrom(Class<?> type) {
        return type.isInstance(this);
    }

    /**
     * 展开为指定对会话类型,通过此方法拿到最原始对会话对象进行操作
     * @param type 类型
     * @param <T> 类型泛型
     * @return 指定类型对会话
     */
    default <T extends DeviceSession> T unwrap(Class<T> type) {
        return type.cast(this);
    }

}
