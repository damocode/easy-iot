package org.damocode.iot.core.server.mqtt;

/**
 * @Description: Mqtt认证信息
 * @Author: zzg
 * @Date: 2021/10/14 8:52
 * @Version: 1.0.0
 */
public interface MqttAuth {

    String getUsername();

    String getPassword();

}
