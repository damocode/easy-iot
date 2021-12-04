package org.damocode.iot.network.mqtt.server;

import io.vertx.mqtt.messages.MqttSubscribeMessage;

/**
 * @Description: 订阅Mqtt
 * @Author: zzg
 * @Date: 2021/10/14 8:57
 * @Version: 1.0.0
 */
public interface MqttSubscription {


    MqttSubscribeMessage getMessage();

    void acknowledge();

}
