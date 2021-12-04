package org.damocode.iot.core.message.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.damocode.iot.core.message.codec.EncodedMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @Description: Http消息
 * @Author: zzg
 * @Date: 2021/10/26 10:43
 * @Version: 1.0.0
 */
public interface HttpMessage extends EncodedMessage {

    List<Header> getHeaders();

    default Optional<Header> getHeader(String name) {
        return getHeaders().stream()
                .filter(header -> header.getName().equals(name))
                .findFirst();
    }

    default String print(StringBuilder builder) {
        ByteBuf payload = getPayload();
        if (payload.readableBytes() == 0) {
            return builder.toString();
        }
        builder.append("\n");
        if (ByteBufUtil.isText(payload, StandardCharsets.UTF_8)) {
            builder.append(payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, payload);
        }
        return builder.toString();
    }

}
