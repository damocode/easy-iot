package org.damocode.iot.core.message.codec.http;

import org.springframework.http.MediaType;

import javax.annotation.Nonnull;

/**
 * @Description: http交换消息
 * @Author: zzg
 * @Date: 2021/10/26 11:03
 * @Version: 1.0.0
 */
public interface HttpExchangeMessage extends HttpRequestMessage {

    void response(HttpResponseMessage message);

    default void ok(String message) {
        response(
                SimpleHttpResponseMessage.builder()
                        .contentType(MediaType.APPLICATION_JSON)
                        .status(200)
                        .body(message)
                        .build()
        );
    }

    default void error(int status, @Nonnull String message) {
        response(SimpleHttpResponseMessage.builder()
                .contentType(MediaType.APPLICATION_JSON)
                .status(status)
                .body(message)
                .build());
    }

    default SimpleHttpResponseMessage.SimpleHttpResponseMessageBuilder newResponse() {
        return SimpleHttpResponseMessage.builder();
    }

}
