package org.damocode.iot.network.tcp.client;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.network.tcp.TcpMessage;
import org.damocode.iot.network.tcp.parser.PayloadParser;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: Tcp客户端实现
 * @Author: zzg
 * @Date: 2021/10/7 14:57
 * @Version: 1.0.0
 */
@Slf4j
public class VertxTcpClient implements TcpClient {

    @Getter
    private final String id;

    private final List<Runnable> disconnectListener = new CopyOnWriteArrayList<>();

    volatile PayloadParser payloadParser;

    public NetSocket socket;

    PublishSubject<TcpMessage> processor = PublishSubject.create();

    @Setter
    private long keepAliveTimeoutMs = Duration.ofMinutes(10).toMillis();

    private volatile long lastKeepAliveTime = System.currentTimeMillis();

    private final boolean serverClient;

    public VertxTcpClient(String id,boolean serverClient){
        this.id = id;
        this.serverClient = serverClient;
    }

    /**
     * 设置客户端消息解析器 并订阅消息
     * @param payloadParser 消息解析器
     */
    public void setRecordParser(PayloadParser payloadParser) {
        synchronized (this) {
            if (null != this.payloadParser && this.payloadParser != payloadParser) {
                this.payloadParser.close();
            }
            this.payloadParser = payloadParser;
            this.payloadParser.handlePayload().subscribe(buffer -> received(new TcpMessage(buffer.getByteBuf())));
        }
    }

    protected void received(TcpMessage message) {
        processor.onNext(message);
    }

    public void setSocket(NetSocket socket) {
        synchronized (this) {
            Objects.requireNonNull(payloadParser);
            if (this.socket != null && this.socket != socket) {
                this.socket.close();
            }
            this.socket = socket
                    .closeHandler(v -> shutdown())
                    .handler(buffer -> {
                        if (log.isDebugEnabled()) {
                            log.debug("handle tcp client[{}] payload:[{}]",
                                    socket.remoteAddress(),
                                    Hex.encodeHexString(buffer.getBytes()));
                        }
                        keepAlive();
                        payloadParser.handle(buffer);
                        if (this.socket != socket) {
                            log.warn("tcp client [{}] memory leak ", socket.remoteAddress());
                            socket.close();
                        }
                    });
        }
    }

    public void keepAlive() {
        lastKeepAliveTime = System.currentTimeMillis();
    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        keepAliveTimeoutMs = timeout.toMillis();
    }

    public boolean isAlive() {
        return socket != null && (keepAliveTimeoutMs < 0 || System.currentTimeMillis() - lastKeepAliveTime < keepAliveTimeoutMs);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        if (null == socket) {
            return null;
        }
        SocketAddress socketAddress = socket.remoteAddress();
        return new InetSocketAddress(socketAddress.host(), socketAddress.port());
    }

    @Override
    public PublishSubject<TcpMessage> subscribe() {
        return processor;
    }

    @Override
    public InetSocketAddress address() {
        return getRemoteAddress();
    }

    public Boolean sendMessage(EncodedMessage message) {
        if (socket == null) {
            log.debug("socket closed");
            return false;
        }
        AtomicBoolean flag = new AtomicBoolean(true);
        Buffer buffer = Buffer.buffer(message.getPayload());
        socket.write(buffer, r -> {
            keepAlive();
            if (!r.succeeded()) {
                flag.set(false);
            }
        });
        return flag.get();
    }

    @Override
    public void disconnect() {
        shutdown();
    }

    @Override
    public Boolean send(TcpMessage message) {
        return sendMessage(message);
    }

    @Override
    public void onDisconnect(Runnable disconnected) {
        disconnectListener.add(disconnected);
    }

    public void shutdown() {
        synchronized (this) {
            log.info("tcp client [{}] disconnect", this);
            if (null != socket) {
                execute(socket::close);
                this.socket = null;
            }
            for (Runnable runnable : disconnectListener) {
                execute(runnable);
            }
            disconnectListener.clear();
            if(serverClient) {
                processor.onCompleted();
            }
        }
    }

    private void execute(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.warn("close tcp client error", e);
        }
    }

}
