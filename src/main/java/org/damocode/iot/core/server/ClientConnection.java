package org.damocode.iot.core.server;

import org.damocode.iot.core.message.codec.EncodedMessage;

import java.net.InetSocketAddress;

/**
 * @Description: 客户端连接
 * @Author: zzg
 * @Date: 2021/10/14 15:24
 * @Version: 1.0.0
 */
public interface ClientConnection {

    /**
     * @return 客户端地址
     */
    InetSocketAddress address();

    /**
     * 发送消息给客户端
     * @param message 消息
     * @return 发送结果
     */
    Boolean sendMessage(EncodedMessage message);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * @return 连接是否还存活
     */
    boolean isAlive();

}
