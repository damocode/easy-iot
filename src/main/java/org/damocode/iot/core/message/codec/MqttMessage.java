package org.damocode.iot.core.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;

/**
 * @Description: Mqtt消息
 * @Author: zzg
 * @Date: 2021/10/12 8:57
 * @Version: 1.0.0
 */
public interface MqttMessage extends EncodedMessage {

    String getTopic();

    String getClientId();

    int getMessageId();

    default boolean isWill() {
        return false;
    }

    default int getQosLevel() {
        return 0;
    }

    default boolean isDup() {
        return false;
    }

    default boolean isRetain() {
        return false;
    }

    default String print() {
        StringBuilder builder = new StringBuilder();
        builder.append("qos").append(this.getQosLevel()).append(" ").append(this.getTopic()).append("\n").append("messageId: ").append(this.getMessageId()).append("\n").append("dup: ").append(this.isDup()).append("\n").append("retain: ").append(this.isRetain()).append("\n").append("will: ").append(this.isWill()).append("\n\n");
        ByteBuf payload = this.getPayload();
        if (ByteBufUtil.isText(payload, StandardCharsets.UTF_8)) {
            builder.append(payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, payload);
        }

        return builder.toString();
    }

}
