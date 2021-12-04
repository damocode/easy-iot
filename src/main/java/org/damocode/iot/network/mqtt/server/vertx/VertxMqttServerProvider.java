package org.damocode.iot.network.mqtt.server.vertx;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Mqtt服务提供者
 * @Author: zzg
 * @Date: 2021/10/14 9:52
 * @Version: 1.0.0
 */
@Slf4j
public class VertxMqttServerProvider {

    private final Vertx vertx;

    public VertxMqttServerProvider(Vertx vertx) {
        this.vertx = vertx;
    }

    public VertxMqttServer createNetwork(VertxMqttServerProperties properties) {
        if(properties.getOptions() == null){
            properties.setOptions(new MqttServerOptions());
        }
        VertxMqttServer server = new VertxMqttServer(properties.getId());
        initServer(server, properties);
        return server;
    }


    private void initServer(VertxMqttServer server, VertxMqttServerProperties properties) {
        List<MqttServer> instances = new ArrayList<>(properties.getInstance());
        for (int i = 0; i < properties.getInstance(); i++) {
            MqttServer mqttServer = MqttServer.create(vertx, properties.getOptions());
            instances.add(mqttServer);
        }
        server.setMqttServer(instances);
        for (MqttServer instance : instances) {
            instance.listen(result -> {
                if (result.succeeded()) {
                    log.debug("startup mqtt server [{}] on port :{} ", properties.getId(), result.result().actualPort());
                } else {
                    log.warn("startup mqtt server [{}] error ", properties.getId(), result.cause());
                }
            });
        }
    }



}
