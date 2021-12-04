package org.damocode.iot.network.http.server;

import io.netty.buffer.ByteBuf;
import org.damocode.iot.core.message.codec.http.Header;
import org.springframework.http.MediaType;

/**
 * @Description: Http响应
 * @Author: zzg
 * @Date: 2021/10/26 10:58
 * @Version: 1.0.0
 */
public interface HttpResponse {

    HttpResponse status(int status);

    HttpResponse contentType(MediaType mediaType);

    HttpResponse header(Header header);

    HttpResponse header(String header, String value);

    void write(ByteBuf buffer);

    void end();

    default void writeAndEnd(ByteBuf buffer) {
        write(buffer);
        end();
    }
}
