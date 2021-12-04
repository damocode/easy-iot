package org.damocode.iot.network.mqtt.server;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import org.damocode.iot.core.message.codec.MqttMessage;
import org.damocode.iot.core.server.mqtt.MqttAuth;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Description: MQTT连接信息,一个MQTT连接就是一个MQTT客户端.
 * @Author: zzg
 * @Date: 2021/10/14 8:51
 * @Version: 1.0.0
 */
public interface MqttConnection {

    /**
     * 获取MQTT客户端ID
     * @return clientId
     */
    String getClientId();

    /**
     * 获取MQTT认证信息
     * @return 可选的MQTT认证信息
     */
    MqttAuth getAuth();

    /**
     * 拒绝MQTT连接
     * @param code 返回码
     */
    void reject(MqttConnectReturnCode code);

    /**
     * 接受连接.接受连接后才能进行消息收发.
     * @return 当前连接信息
     */
    MqttConnection accept();

    /**
     * 获取遗言消息
     * @return 可选的遗言信息
     */
    Optional<MqttMessage> getWillMessage();

    /**
     * 订阅客户端推送的消息
     * @return 消息流
     */
    PublishSubject<MqttPublishing> handleMessage();

    /**
     * 推送消息到客户端
     * @param message MQTT消息
     * @return 异步推送结果
     */
    Boolean publish(MqttMessage message);

    /**
     * 订阅客户端订阅请求
     * @param autoAck 是否自动应答
     * @return 订阅请求流
     */
    PublishSubject<MqttSubscription> handleSubscribe(boolean autoAck);

    /**
     * 订阅客户端取消订阅请求
     * @param autoAck 是否自动应答
     * @return 取消订阅请求流
     */
    PublishSubject<MqttUnSubscription> handleUnSubscribe(boolean autoAck);

    /**
     * 监听断开连接
     * @param listener 监听器
     */
    void onClose(Consumer<MqttConnection> listener);

    /**
     * 获取MQTT连接是否存活,当客户端断开连接或者 客户端ping超时后则返回false.
     * @return mqtt连接是否存活
     */
    boolean isAlive();

    /**
     * 关闭mqtt连接
     */
    void close();

    long getLastPingTime();

    void keepAlive();

    void setKeepAliveTimeout(Duration duration);

    InetSocketAddress getClientAddress();
}
