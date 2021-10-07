package org.damocode.iot.core.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * @Description: 建议消息编码器
 * @Author: zzg
 * @Date: 2021/10/7 14:43
 * @Version: 1.0.0
 */
@AllArgsConstructor
@Getter
public class SimpleEncodedMessage implements EncodedMessage {

    private final ByteBuf payload;

    private final MessagePayloadType payloadType;

    public static SimpleEncodedMessage of(ByteBuf byteBuf, MessagePayloadType payloadType) {
        return new SimpleEncodedMessage(byteBuf, payloadType);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        if (ByteBufUtil.isText(payload, StandardCharsets.UTF_8)) {
            builder.append(payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, payload);
        }
        return builder.toString();
    }
}

