package org.damocode.iot.network.mqtt.server;

import org.damocode.iot.core.message.codec.MqttMessage;

/**
 * @Description: 客户端推送的消息
 * @Author: zzg
 * @Date: 2021/10/14 8:56
 * @Version: 1.0.0
 */
public interface MqttPublishing {

    MqttMessage getMessage();

    void acknowledge();

}
