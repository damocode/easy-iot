package org.damocode.iot.network.mqtt.server.vertx;

import io.vertx.mqtt.MqttServerOptions;
import lombok.*;

/**
 * @Description: Mqtt服务端属性
 * @Author: zzg
 * @Date: 2021/10/14 9:02
 * @Version: 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VertxMqttServerProperties {

    private String id;

    //服务实例数量(线程数)
    private int instance = 4;

    private String certId;

    private boolean ssl;

    private MqttServerOptions options;

}
