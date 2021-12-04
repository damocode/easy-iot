package org.damocode.iot.network.http.server;

import io.netty.buffer.ByteBuf;
import org.damocode.iot.core.message.codec.http.Header;
import org.damocode.iot.core.message.codec.http.HttpRequestMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @Description: Http请求
 * @Author: zzg
 * @Date: 2021/10/26 10:58
 * @Version: 1.0.0
 */
public interface HttpRequest {

    String getUrl();

    String getRealIp();

    InetSocketAddress getClientAddress();

    HttpMethod getMethod();

    MediaType getContentType();

    Map<String, String> getQueryParameters();

    Map<String, String> getRequestParam();

    PublishSubject<ByteBuf> getBody();

    List<Header> getHeaders();

    Observable<HttpRequestMessage> toMessage();

}
