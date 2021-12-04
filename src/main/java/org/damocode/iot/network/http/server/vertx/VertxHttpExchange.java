package org.damocode.iot.network.http.server.vertx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.SocketAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.message.codec.http.Header;
import org.damocode.iot.core.message.codec.http.HttpRequestMessage;
import org.damocode.iot.network.http.DefaultHttpRequestMessage;
import org.damocode.iot.network.http.VertxWebUtils;
import org.damocode.iot.network.http.server.HttpExchange;
import org.damocode.iot.network.http.server.HttpRequest;
import org.damocode.iot.network.http.server.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: zzg
 * @Date: 2021/10/26 11:22
 * @Version: 1.0.0
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class VertxHttpExchange implements HttpExchange, HttpResponse, HttpRequest {

    private final HttpServerRequest httpServerRequest;
    private final HttpServerResponse response;
    private final PublishSubject<ByteBuf> body;

    public VertxHttpExchange(HttpServerRequest httpServerRequest, HttpServerConfig config) {
        this.httpServerRequest = httpServerRequest;
        this.response = httpServerRequest.response();
        config.getHttpHeaders().forEach(response::putHeader);
        body = PublishSubject.create();
        if (httpServerRequest.method() == HttpMethod.GET) {
            body.onNext(Unpooled.EMPTY_BUFFER);
        } else {
            if(httpServerRequest.isEnded()){
                body.onCompleted();
            }else {
                httpServerRequest.bodyHandler(buffer -> body.onNext(buffer.getByteBuf()));
            }
            body.subscribe();
        }
    }

    @Override
    public HttpRequest request() {
        return this;
    }

    @Override
    public HttpResponse response() {
        return this;
    }

    @Override
    public boolean isClosed() {
        return response.closed() || response.ended();
    }

    @Override
    public HttpResponse status(int status) {
        response.setStatusCode(status);
        return this;
    }

    private Map<String, String> convertRequestParam(MultiMap multiMap) {
        return multiMap.entries()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> String.join(",", a, b)));
    }

    private List<Header> convertHeader(MultiMap multiMap) {
        return multiMap.entries()
                .stream()
                .map(entry -> {
                    Header header = new Header();
                    header.setName(entry.getKey());
                    header.setValue(new String[]{entry.getValue()});
                    return header;
                })
                .collect(Collectors.toList())
                ;
    }

    private org.springframework.http.HttpMethod convertMethodType(HttpMethod method) {
        for (org.springframework.http.HttpMethod httpMethod : org.springframework.http.HttpMethod.values()) {
            if (httpMethod.toString().equals(method.toString())) {
                return httpMethod;
            }
        }
        throw new UnsupportedOperationException("不支持的HttpMethod类型: " + method);
    }

    private void setResponseDefaultLength(int length) {
        if (!isClosed()) {
            response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
        }
    }

    @Override
    public HttpResponse contentType(MediaType mediaType) {
        if (null != mediaType && !isClosed()) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        }
        return this;
    }

    @Override
    public HttpResponse header(Header header) {
        if (null != header && !isClosed()) {
            response.putHeader(header.getName(), Arrays.<String>asList(header.getValue()));
        }
        return this;
    }

    @Override
    public HttpResponse header(String header, String value) {
        if (header != null && value != null && !isClosed()) {
            response.putHeader(header, value);
        }
        return this;
    }

    @Override
    public void write(ByteBuf buffer) {
        if (isClosed()) {
            return;
        }
        Buffer buf = Buffer.buffer(buffer);
        setResponseDefaultLength(buf.length());
        response.write(buf);
    }

    @Override
    public void end() {
        if (isClosed()) {
            return;
        }
        if (response.ended()) {
            return;
        }
        response.end();
    }

    @Override
    public String getUrl() {
        return httpServerRequest.path();
    }

    @Override
    public String getRealIp() {
        return VertxWebUtils.getIpAddr(httpServerRequest);
    }

    @Override
    public InetSocketAddress getClientAddress() {
        SocketAddress address = httpServerRequest.remoteAddress();
        if (null == address) {
            return null;
        }
        return new InetSocketAddress(getRealIp(), address.port());
    }

    @Override
    public MediaType getContentType() {
        String contentType = httpServerRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.hasText(contentType)) {
            return MediaType.parseMediaType(contentType);
        } else {
            return MediaType.APPLICATION_FORM_URLENCODED;
        }
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> params = new HashMap<>();
        MultiMap map = httpServerRequest.params();
        for (String name : map.names()) {
            params.put(name, String.join(",", map.getAll(name)));
        }
        return params;
    }

    @Override
    public Map<String, String> getRequestParam() {
        return convertRequestParam(httpServerRequest.formAttributes());
    }

    @Override
    public PublishSubject<ByteBuf> getBody() {
        return body;
    }

    @Override
    public org.springframework.http.HttpMethod getMethod() {
        return convertMethodType(httpServerRequest.method());
    }

    @Override
    public List<Header> getHeaders() {
        return convertHeader(httpServerRequest.headers());
    }

    @Override
    public Observable<HttpRequestMessage> toMessage() {
        return this.getBody().map(byteBuf -> {
            DefaultHttpRequestMessage message = new DefaultHttpRequestMessage();
            message.setContentType(this.getContentType());
            message.setHeaders(this.getHeaders());
            message.setMethod(this.getMethod());
            message.setPayload(byteBuf);
            message.setQueryParameters(this.getQueryParameters());
            message.setUrl(this.getUrl());
            return message;
        });
    }

}
