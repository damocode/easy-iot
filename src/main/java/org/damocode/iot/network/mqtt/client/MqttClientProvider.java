package org.damocode.iot.network.mqtt.client;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: Mqtt客户端提供者
 * @Author: zzg
 * @Date: 2021/10/12 9:52
 * @Version: 1.0.0
 */
@Slf4j
public class MqttClientProvider {

    private final Vertx vertx;


    public MqttClientProvider(Vertx vertx) {
        this.vertx = vertx;
    }

    public VertxMqttClient createNetwork(MqttClientProperties properties) {
        if(properties.getOptions() == null){
            properties.setOptions(new MqttClientOptions());
        }
        VertxMqttClient mqttClient = new VertxMqttClient(properties.getId());
        initMqttClient(mqttClient, properties);
        return mqttClient;
    }

    private void initMqttClient(VertxMqttClient mqttClient, MqttClientProperties properties) {
        mqttClient.setLoading(true);
        io.vertx.mqtt.MqttClient client = MqttClient.create(vertx, properties.getOptions());
        mqttClient.setClient(client);
        client.connect(properties.getPort(), properties.getHost(), result -> {
            mqttClient.setLoading(false);
            if (!result.succeeded()) {
                log.warn("connect mqtt [{}] error", properties.getId(), result.cause());
            } else {
                log.debug("connect mqtt [{}] success", properties.getId());
            }
        });
    }

}
