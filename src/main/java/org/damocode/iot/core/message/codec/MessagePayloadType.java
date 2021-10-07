package org.damocode.iot.core.message.codec;

/**
 * @Description: 消息载荷类型
 * @Author: zzg
 * @Date: 2021/10/7 14:43
 * @Version: 1.0.0
 */
public enum MessagePayloadType {

    JSON, STRING, BINARY, HEX, UNKNOWN;

    public static MessagePayloadType of(String of) {
        for (MessagePayloadType value : MessagePayloadType.values()) {
            if (value.name().equalsIgnoreCase(of)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
