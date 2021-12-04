package org.damocode.iot.network.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.damocode.iot.core.message.codec.http.HttpExchangeMessage;
import org.damocode.iot.core.message.codec.http.HttpResponseMessage;
import org.damocode.iot.core.message.codec.http.SimpleHttpResponseMessage;
import org.damocode.iot.network.http.gateway.HttpServerExchangeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import rx.subjects.PublishSubject;

import java.util.function.Consumer;

/**
 * @Description:
 * @Author: zzg
 * @Date: 2021/10/26 10:57
 * @Version: 1.0.0
 */
public interface HttpExchange {

    HttpRequest request();

    HttpResponse response();

    boolean isClosed();

    default void error(HttpStatus status) {
        response(status, "{\"message\":\"" + status.getReasonPhrase() + "\"}");
    }

    default void ok() {
        response(HttpStatus.OK, "{\"message\":\"OK\"}");
    }

    default void response(HttpStatus status, String body) {
        this.response(SimpleHttpResponseMessage.builder()
                .contentType(MediaType.APPLICATION_JSON)
                .status(status.value())
                .body(body.getBytes())
                .build());
    }

    default void error(HttpStatus status, Throwable body) {
        response(status, body.getMessage() == null ? body.getClass().getSimpleName() : body.getMessage());
    }

    default void response(HttpResponseMessage message) {
        HttpResponse response = response();
        response.status(message.getStatus());
        if (!CollectionUtils.isEmpty(message.getHeaders())) {
            message.getHeaders().forEach(response::header);
        }
        response.contentType(message.getContentType());
        response.writeAndEnd(message.getPayload());
    }

    default PublishSubject<ByteBuf> getByteBuf(){
        return request().getBody();
    }

    default void toExchangeMessage(Consumer<HttpExchangeMessage> consumer) {
        PublishSubject<ByteBuf> subject = request().getBody();
        HttpExchange that  = this;
        subject.subscribe(byteBuf -> {
            HttpServerExchangeMessage httpServerExchangeMessage = new HttpServerExchangeMessage(that, byteBuf == null ? Unpooled.EMPTY_BUFFER : byteBuf);
            consumer.accept(httpServerExchangeMessage);
        });
    }

}
