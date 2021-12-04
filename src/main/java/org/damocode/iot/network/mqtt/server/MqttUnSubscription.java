package org.damocode.iot.network.mqtt.server;

import io.vertx.mqtt.messages.MqttUnsubscribeMessage;

/**
 * @Description: 取消Mqtt订阅
 * @Author: zzg
 * @Date: 2021/10/14 8:59
 * @Version: 1.0.0
 */
public interface MqttUnSubscription {

    MqttUnsubscribeMessage getMessage();

    void acknowledge();

}
