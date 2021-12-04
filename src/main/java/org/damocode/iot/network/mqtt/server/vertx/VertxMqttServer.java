package org.damocode.iot.network.mqtt.server.vertx;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.network.mqtt.server.MqttConnection;
import org.damocode.iot.network.mqtt.server.MqttServer;
import rx.subjects.PublishSubject;

import java.util.Collection;

/**
 * @Description: Mqtt服务端实现类
 * @Author: zzg
 * @Date: 2021/10/14 9:44
 * @Version: 1.0.0
 */
@Slf4j
public class VertxMqttServer implements MqttServer {

    @Getter
    private final String id;

    private PublishSubject<MqttConnection> connectionProcessor =PublishSubject.create();

    private Collection<io.vertx.mqtt.MqttServer> mqttServer;

    public VertxMqttServer(String id) {
        this.id = id;
    }

    @Override
    public boolean isAlive() {
        return mqttServer != null && !mqttServer.isEmpty();
    }

    @Override
    public void shutdown() {
        if (mqttServer != null) {
            for (io.vertx.mqtt.MqttServer server : mqttServer) {
                server.close(res -> {
                    if (res.failed()) {
                        log.error(res.cause().getMessage(), res.cause());
                    } else {
                        log.debug("mqtt server [{}] closed", server.actualPort());
                    }
                });
            }
            mqttServer.clear();
        }
    }

    public void setMqttServer(Collection<io.vertx.mqtt.MqttServer> mqttServer) {
        if (this.mqttServer != null && !this.mqttServer.isEmpty()) {
            shutdown();
        }
        this.mqttServer = mqttServer;
        for (io.vertx.mqtt.MqttServer server : this.mqttServer) {
            server
                    .exceptionHandler(error -> {
                        log.error(error.getMessage(), error);
                    })
                    .endpointHandler(endpoint -> {
                        if (!connectionProcessor.hasObservers()) {
                            log.info("mqtt server no handler for:[{}]", endpoint.clientIdentifier());
                            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
                            return;
                        }
                        connectionProcessor.onNext(new VertxMqttConnection(endpoint));
                    });
        }
    }

    @Override
    public PublishSubject<MqttConnection> handleConnection() {
        return connectionProcessor;
    }
}
