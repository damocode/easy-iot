package org.damocode.iot.network.http;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.damocode.iot.core.message.codec.http.Header;
import org.damocode.iot.core.message.codec.http.HttpResponseMessage;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 默认的http响应消息
 * @Author: zzg
 * @Date: 2021/10/26 10:47
 * @Version: 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultHttpResponseMessage implements HttpResponseMessage {

    private int status;

    private MediaType contentType;

    private List<Header> headers = new ArrayList<>();

    private ByteBuf payload;

    @Override
    public String toString() {
        return print();
    }

}
