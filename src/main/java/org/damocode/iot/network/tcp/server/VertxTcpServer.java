package org.damocode.iot.network.tcp.server;

import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.network.tcp.client.TcpClient;
import org.damocode.iot.network.tcp.client.VertxTcpClient;
import org.damocode.iot.network.tcp.parser.PayloadParser;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @Description: Tcp服务实现
 * @Author: zzg
 * @Date: 2021/10/7 15:02
 * @Version: 1.0.0
 */
@Slf4j
public class VertxTcpServer implements TcpServer {

    @Getter
    private final String id;

    Collection<NetServer> tcpServers;

    private Supplier<PayloadParser> parserSupplier;

    PublishSubject<TcpClient> subject = PublishSubject.create();

    @Setter
    private long keepAliveTimeout = Duration.ofMinutes(10).toMillis();

    public VertxTcpServer(String id) {
        this.id = id;
    }

    public void setServer(Collection<NetServer> servers) {
        if (this.tcpServers != null && !this.tcpServers.isEmpty()) {
            shutdown();
        }
        this.tcpServers = servers;
        for (NetServer tcpServer : this.tcpServers) {
            tcpServer.connectHandler(this::acceptTcpConnection);
        }
    }

    @Override
    public boolean isAlive() {
        return tcpServers != null;
    }

    public void shutdown() {
        if (null != tcpServers) {
            for (NetServer tcpServer : tcpServers) {
                execute(tcpServer::close);
            }
            tcpServers = null;
        }
    }

    private void execute(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.warn("close tcp server error", e);
        }
    }

    protected void acceptTcpConnection(NetSocket socket) {
        if (!subject.hasObservers()) {
            log.warn("not handler for tcp client[{}]", socket.remoteAddress());
            socket.close();
            return;
        }
        VertxTcpClient client = new VertxTcpClient(id + "_" + socket.remoteAddress(), true);
        client.setKeepAliveTimeoutMs(keepAliveTimeout);
        try {
            // TCP异常和关闭处理
            socket.exceptionHandler(err -> {
                log.error("tcp server client [{}] error", socket.remoteAddress(), err);
            }).closeHandler((nil) -> {
                log.debug("tcp server client [{}] closed", socket.remoteAddress());
                client.shutdown();
            });
            client.setRecordParser(parserSupplier.get());
            client.setSocket(socket);
            // 发布给订阅者
            subject.onNext(client);
            log.debug("accept tcp client [{}] connection", socket.remoteAddress());
        } catch (Exception e) {
            log.error("create tcp server client error", e);
            client.shutdown();
        }
    }

    public void setParserSupplier(Supplier<PayloadParser> parserSupplier) {
        this.parserSupplier = parserSupplier;
    }

    @Override
    public PublishSubject<TcpClient> handleConnection() {
        return subject;
    }
}
