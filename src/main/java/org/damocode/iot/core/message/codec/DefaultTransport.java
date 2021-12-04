package org.damocode.iot.core.message.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Description: 默认传输协议定义
 * @Author: zzg
 * @Date: 2021/10/14 11:20
 * @Version: 1.0.0
 */
@AllArgsConstructor
public enum DefaultTransport implements Transport{

    MQTT("MQTT"),
    TCP("TCP"),
    HTTP("HTTP")
    ;

    static {
        Transports.register(Arrays.asList(DefaultTransport.values()));
    }

    @Getter
    private final String name;

    @Override
    public String getId() {
        return name();
    }

}
