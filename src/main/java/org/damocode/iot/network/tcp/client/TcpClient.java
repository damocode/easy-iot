package org.damocode.iot.network.tcp.client;

import org.damocode.iot.core.server.ClientConnection;
import org.damocode.iot.network.Network;
import org.damocode.iot.network.tcp.TcpMessage;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * @Description: Tcp客户端
 * @Author: zzg
 * @Date: 2021/10/7 14:56
 * @Version: 1.0.0
 */
public interface TcpClient extends Network, ClientConnection {

    /**
     * 获取客户端远程地址
     * @return 客户端远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 订阅TCP消息
     * @return
     */
    PublishSubject<TcpMessage> subscribe();

    /**
     * 向客户端发送数据
     * @param message 数据对象
     * @return 发送结果
     */
    Boolean send(TcpMessage message);

    /**
     * 断开连接
     * @param disconnected
     */
    void onDisconnect(Runnable disconnected);

    /**
     * 连接保活
     */
    void keepAlive();

    /**
     * 设置客户端心跳超时时间
     *
     * @param timeout 超时时间
     */
    void setKeepAliveTimeout(Duration timeout);

}
