package org.damocode.iot.network.http.server.vertx;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.topic.Topic;
import org.damocode.iot.network.http.server.HttpExchange;
import org.damocode.iot.network.http.server.HttpServer;
import org.springframework.http.HttpStatus;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: Http服务实现类
 * @Author: zzg
 * @Date: 2021/10/26 11:18
 * @Version: 1.0.0
 */
@Slf4j
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VertxHttpServer implements HttpServer {

    private Collection<io.vertx.core.http.HttpServer> httpServers;

    private HttpServerConfig config;

    private String id;

    private final Topic<PublishSubject<HttpExchange>> route = Topic.createRoot();

    private PublishSubject<HttpExchange> httpServerSubject = PublishSubject.create();

    @Getter
    @Setter
    private String lastError;

    @Setter(AccessLevel.PACKAGE)
    private InetSocketAddress bindAddress;

    public VertxHttpServer(HttpServerConfig config) {
        this.config = config;
        this.id = config.getId();
    }

    @Override
    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    @Override
    public PublishSubject<HttpExchange> handleRequest() {
        return this.httpServerSubject;
    }

    @Override
    public PublishSubject<HttpExchange> handleRequest(String method, Action1<HttpExchange> action1, String... urlPatterns) {
        PublishSubject<HttpExchange> subject = PublishSubject.create();
        List<Runnable> list = new ArrayList<>();
        for (String urlPattern : urlPatterns) {
            String pattern = Stream.of(urlPattern.split("[/]"))
                    .map(str -> {
                        if (str.startsWith("{") && str.endsWith("}")) {
                            return "*";
                        }
                        return str;
                    })
                    .collect(Collectors.joining("/"));
            if (pattern.endsWith("/")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            if (!pattern.startsWith("/")) {
                pattern = "/".concat(pattern);
            }
            pattern = "/" + method + pattern;
            log.debug("handle http request : {}", pattern);
            Topic<PublishSubject<HttpExchange>> sub = route.append(pattern);
            sub.subscribe(subject);
            list.add(() -> sub.unsubscribe(subject));
        }
        subject.subscribe(action1,throwable -> {},() -> list.forEach(runnable -> runnable.run()));
        return subject;
    }

    @Override
    public void shutdown() {
        if (httpServers != null) {
            for (io.vertx.core.http.HttpServer httpServer : httpServers) {
                httpServer.close(res -> {
                    if (res.failed()) {
                        log.error(res.cause().getMessage(), res.cause());
                    } else {
                        log.debug("http server [{}] closed", httpServer.actualPort());
                    }
                });
            }
            httpServers.clear();
            httpServers = null;
        }
    }

    public void setHttpServers(Collection<io.vertx.core.http.HttpServer> httpServers) {
        if (isAlive()) {
            shutdown();
        }
        this.httpServers = httpServers;
        for (io.vertx.core.http.HttpServer server : this.httpServers) {
            server.requestHandler(request -> {
                request.exceptionHandler(err -> log.error(err.getMessage(), err));
                VertxHttpExchange exchange = new VertxHttpExchange(request, config);
                String url = exchange.getUrl();
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                route.findTopic("/" + exchange.request().getMethod().name().toLowerCase() + url)
                        .subscribe(subjectTopic -> {
                            Set<PublishSubject<HttpExchange>> sets = subjectTopic.getSubscribers();
                            if(sets.isEmpty()){
                                if (!httpServerSubject.hasObservers()) {
                                    log.warn("http server no handler for:[{} {}://{}{}]", request.method(), request.scheme(), request.host(), request.path());
                                    request.response()
                                            .setStatusCode(HttpStatus.NOT_FOUND.value())
                                            .end();
                                }
                            }
                            sets.forEach(subject -> {
                                subject.onNext(exchange);
                            });
                        });
                if (httpServerSubject.hasObservers()) {
                    httpServerSubject.onNext(exchange);
                }
            });
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isAlive() {
        return httpServers != null && !httpServers.isEmpty();
    }

}
