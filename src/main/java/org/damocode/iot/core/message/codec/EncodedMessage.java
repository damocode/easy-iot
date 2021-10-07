package org.damocode.iot.core.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;

/**
 * @Description: 消息编码
 * @Author: zzg
 * @Date: 2021/10/7 14:42
 * @Version: 1.0.0
 */
public interface EncodedMessage {

    ByteBuf getPayload();

    default String payloadAsString() {
        return getPayload().toString(StandardCharsets.UTF_8);
    }

    default byte[] payloadAsBytes() {
        return ByteBufUtil.getBytes(getPayload());
    }

    default byte[] getBytes(int offset, int len) {
        return ByteBufUtil.getBytes(getPayload(), offset, len);
    }

    static EncodedMessage simple(ByteBuf data) {
        return simple(data, MessagePayloadType.BINARY);
    }

    static EncodedMessage simple(ByteBuf data, MessagePayloadType payloadType) {
        return SimpleEncodedMessage.of(data, payloadType);
    }

}
