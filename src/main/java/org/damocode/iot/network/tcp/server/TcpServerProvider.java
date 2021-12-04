package org.damocode.iot.network.tcp.server;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Tcp服务提供器
 * @Author: zzg
 * @Date: 2021/10/7 15:02
 * @Version: 1.0.0
 */
@Slf4j
public class TcpServerProvider {

    private final Vertx vertx;

    public TcpServerProvider(Vertx vertx) {
        this.vertx = vertx;
    }

    public VertxTcpServer createNetwork(TcpServerProperties properties) {
        if(properties.getOptions() == null){
            properties.setOptions(new NetServerOptions());
        }
        VertxTcpServer tcpServer = new VertxTcpServer(properties.getId());
        initTcpServer(tcpServer, properties);
        return tcpServer;
    }

    private void initTcpServer(VertxTcpServer tcpServer,TcpServerProperties properties) {
        int instance = Math.max(2, properties.getInstance());
        List<NetServer> instances = new ArrayList<>(instance);
        for (int i = 0; i < instance; i++) {
            instances.add(vertx.createNetServer(properties.getOptions()));
        }
        // 根据解析类型配置数据解析器
        tcpServer.setParserSupplier(properties.getParserSupplier());
        tcpServer.setServer(instances);
        Long keepAliveTimeout = properties.getKeepAliveTimeout();
        if(keepAliveTimeout != null){
            tcpServer.setKeepAliveTimeout(properties.getKeepAliveTimeout());
        }
        // 针对JVM做的多路复用优化
        // 多个server listen同一个端口，每个client连接的时候vertx会分配
        // 一个connection只能在一个server中处理
        for (NetServer netServer : instances) {
            netServer.listen(properties.createSocketAddress(), result -> {
                if (result.succeeded()) {
                    log.info("tcp server startup on {}", result.result().actualPort());
                } else {
                    log.error("startup tcp server error: {}", result.cause());
                }
            });
        }
    }

}
