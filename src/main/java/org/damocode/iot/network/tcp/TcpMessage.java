package org.damocode.iot.network.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.damocode.iot.core.message.codec.EncodedMessage;

/**
 * @Description: Tcp协议消息
 * @Author: zzg
 * @Date: 2021/10/7 14:55
 * @Version: 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TcpMessage implements EncodedMessage {

    private ByteBuf payload;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ByteBufUtil.appendPrettyHexDump(builder,payload);
        return builder.toString();
    }
}
