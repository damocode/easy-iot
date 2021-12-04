package org.damocode.iot.network.mqtt.server;

import org.damocode.iot.network.Network;
import rx.subjects.PublishSubject;

/**
 * @Description: Mqtt服务端
 * @Author: zzg
 * @Date: 2021/10/14 8:49
 * @Version: 1.0.0
 */
public interface MqttServer extends Network {

    /**
     * 订阅客户端连接
     * @return 客户端连接
     */
    PublishSubject<MqttConnection> handleConnection();

}
