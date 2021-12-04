package org.damocode.iot.network.mqtt.client;

import org.damocode.iot.core.message.codec.MqttMessage;
import org.damocode.iot.network.Network;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.util.List;

/**
 * @Description: Mqtt客户端
 * @Author: zzg
 * @Date: 2021/10/12 8:54
 * @Version: 1.0.0
 */
public interface MqttClient extends Network {

    default PublishSubject<MqttMessage> subscribe(List<String> topics, Action1<MqttMessage> action){
        return subscribe(topics,0,action);
    }

    PublishSubject<MqttMessage> subscribe(List<String> topics, int qos, Action1<MqttMessage> action);

    void publish(MqttMessage message);

}
