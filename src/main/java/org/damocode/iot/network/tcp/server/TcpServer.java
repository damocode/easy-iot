package org.damocode.iot.network.tcp.server;

import org.damocode.iot.network.Network;
import org.damocode.iot.network.tcp.client.TcpClient;
import rx.subjects.PublishSubject;

/**
 * @Description: TCP服务
 * @Author: zzg
 * @Date: 2021/10/7 15:01
 * @Version: 1.0.0
 */
public interface TcpServer extends Network {

    /**
     * 订阅客户端连接
     * @return 客户端流
     */
    PublishSubject<TcpClient> handleConnection();

}
