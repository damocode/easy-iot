package org.damocode.iot.network.http.server.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Http服务提供者
 * @Author: zzg
 * @Date: 2021/10/26 11:28
 * @Version: 1.0.0
 */
@Slf4j
public class HttpServerProvider {

    private final Vertx vertx;

    public HttpServerProvider(Vertx vertx) {
        this.vertx = vertx;
    }

    public VertxHttpServer createNetwork(HttpServerConfig config) {
        if (config.getOptions() == null) {
            config.setOptions(new HttpServerOptions());
        }
        config.getOptions().setPort(config.getPort());
        config.getOptions().setHost(config.getHost());
        VertxHttpServer server = new VertxHttpServer(config);
        initServer(server, config);
        return server;
    }

    private void initServer(VertxHttpServer server, HttpServerConfig config) {
        List<HttpServer> instances = new ArrayList<>(config.getInstance());
        for (int i = 0; i < config.getInstance(); i++) {
            HttpServer httpServer = createHttpServer(config);
            instances.add(httpServer);
        }
        server.setBindAddress(new InetSocketAddress(config.getHost(), config.getPort()));
        server.setHttpServers(instances);
        for (HttpServer httpServer : instances) {
            httpServer.listen(result -> {
                if (result.succeeded()) {
                    log.debug("startup http server on [{}]", server.getBindAddress());
                } else {
                    server.setLastError(result.cause().getMessage());
                    log.warn("startup http server on [{}] failed", server.getBindAddress(), result.cause());
                }
            });
        }
    }

    protected HttpServer createHttpServer(HttpServerConfig config) {
        return vertx.createHttpServer(config.getOptions());
    }


}
