package org.damocode.iot.network.http.server;

import org.damocode.iot.network.Network;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;

/**
 * @Description: Http服务
 * @Author: zzg
 * @Date: 2021/10/26 10:57
 * @Version: 1.0.0
 */
public interface HttpServer extends Network {

    InetSocketAddress getBindAddress();

    PublishSubject<HttpExchange> handleRequest();

    PublishSubject<HttpExchange> handleRequest(String method, Action1<HttpExchange> action1, String... urlPatterns);

    void shutdown();

}
