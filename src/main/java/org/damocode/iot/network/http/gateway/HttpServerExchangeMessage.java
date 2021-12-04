package org.damocode.iot.network.http.gateway;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.message.codec.http.Header;
import org.damocode.iot.core.message.codec.http.HttpExchangeMessage;
import org.damocode.iot.core.message.codec.http.HttpResponseMessage;
import org.damocode.iot.network.http.server.HttpExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description:
 * @Author: zzg
 * @Date: 2021/10/26 11:11
 * @Version: 1.0.0
 */
@Slf4j
public class HttpServerExchangeMessage implements HttpExchangeMessage {

    AtomicReference<Boolean> responded = new AtomicReference<>(false);

    private final HttpExchange exchange;
    private final ByteBuf payload;

    public HttpServerExchangeMessage(HttpExchange exchange, ByteBuf payload) {
        this.exchange = exchange;
        this.payload = payload;
    }

    @Override
    public void response(HttpResponseMessage message) {
        if (!responded.getAndSet(true) && !exchange.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug("响应HTTP请求:\n{}", message.print());
            }
            exchange.response(message);
        }
    }

    @Override
    public String getUrl() {
        return exchange.request().getUrl();
    }

    @Override
    public HttpMethod getMethod() {
        return exchange.request().getMethod();
    }

    @Override
    public MediaType getContentType() {
        return exchange.request().getContentType();
    }

    @Override
    public List<Header> getHeaders() {
        return exchange.request().getHeaders();
    }

    @Override
    public Map<String, String> getQueryParameters() {
        return exchange.request().getQueryParameters();
    }

    @Override
    public ByteBuf getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return print();
    }
}
