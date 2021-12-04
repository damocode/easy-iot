package org.damocode.iot.network.mqtt.client;

import io.vertx.mqtt.MqttClientOptions;
import lombok.*;

/**
 * @Description: Mqtt配置属性
 * @Author: zzg
 * @Date: 2021/10/12 8:59
 * @Version: 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttClientProperties {

    private String id;
    private String host;
    private int port;

    private String certId;
    private MqttClientOptions options;

}
