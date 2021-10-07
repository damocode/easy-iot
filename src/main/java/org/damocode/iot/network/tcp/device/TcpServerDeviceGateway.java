package org.damocode.iot.network.tcp.device;

import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.FromDeviceMessageContext;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.core.server.session.DeviceSessionManager;
import org.damocode.iot.network.tcp.TcpMessage;
import org.damocode.iot.network.tcp.client.TcpClient;
import org.damocode.iot.network.tcp.server.TcpServer;
import org.damocode.iot.network.utils.DeviceGatewayHelper;
import rx.Subscription;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Description: Tcp服务设备网关
 * @Author: zzg
 * @Date: 2021/10/7 15:05
 * @Version: 1.0.0
 */
@Slf4j
public class TcpServerDeviceGateway {

    private final TcpServer tcpServer;

    private final DeviceMessageCodec deviceMessageCodec;

    private final DeviceOperatorManager deviceOperatorManager;

    private final AtomicBoolean started = new AtomicBoolean();

    private final DeviceGatewayHelper helper;

    PublishSubject<Message> subject = PublishSubject.create();

    Subscription subscription;

    public TcpServerDeviceGateway(TcpServer tcpServer, DeviceSessionManager sessionManager, DeviceMessageCodec deviceMessageCodec, DeviceOperatorManager deviceOperatorManager, DecodedClientMessageHandler messageHandler){
        this.tcpServer = tcpServer;
        this.deviceMessageCodec = deviceMessageCodec;
        this.deviceOperatorManager = deviceOperatorManager;
        this.helper = new DeviceGatewayHelper(deviceOperatorManager,sessionManager,messageHandler);
    }

    public void startup() {
        doStart();
    }

    private void doStart() {
        if (started.getAndSet(true) && subscription != null) {
            return;
        }
        PublishSubject<TcpClient> connection = tcpServer.handleConnection();
        subscription = connection.subscribe(client -> {
            log.debug("客户端链接{}被监听",client.getRemoteAddress());
            new TcpConnection(client).accept();
        });
    }

    public PublishSubject<Message> onMessage() {
        return subject;
    }

    class TcpConnection {

        final TcpClient client;
        //消息订阅
        Subscription subscription;
        final AtomicReference<Duration> keepaliveTimeout = new AtomicReference<>();
        final AtomicReference<DeviceSession> sessionRef = new AtomicReference<>();
        final InetSocketAddress address;

        TcpConnection(TcpClient client) {
            this.client = client;
            this.address = client.getRemoteAddress();
            client.onDisconnect(() -> {
                //取消消息订阅
                subscription.unsubscribe();
            });
            //通过客户端id获取设备会话
            DeviceSession session = new UnknownTcpDeviceSession(client.getId(),client,deviceMessageCodec) {
                @Override
                public Boolean send(EncodedMessage encodedMessage) {
                    return super.send(encodedMessage);
                }

                @Override
                public void setKeepAliveTimeout(Duration timeout) {
                    keepaliveTimeout.set(timeout);
                    client.setKeepAliveTimeout(timeout);
                }

                @Override
                public Optional<InetSocketAddress> getClientAddress() {
                    return Optional.of(address);
                }
            };
            sessionRef.set(session);
        }

        // 接收消息
        void accept() {
            PublishSubject<TcpMessage> publishSubject = client.subscribe();
            subscription = publishSubject.subscribe(tcpMessage -> {
                //判断网关是否启动
                if (started.get()) {
                    try{
                        handleTcpMessage(tcpMessage);
                    }catch (Exception err) {
                        log.error(err.getMessage(), err);
                        client.shutdown();
                    }
                }
            });
        }

        void handleTcpMessage(TcpMessage message) {
            if(deviceMessageCodec == null){
                return;
            }
            DeviceMessage deviceMessage = deviceMessageCodec.decode(FromDeviceMessageContext.of(sessionRef.get(), message));
            if(deviceMessage == null) {
                return;
            }
            handleDeviceMessage(deviceMessage);
        }

        void handleDeviceMessage(DeviceMessage message) {
            if(subject.hasObservers()){
                subject.onNext(message);
            }
            Function<DeviceOperator, DeviceSession> sessionBuilder = device -> new TcpDeviceSession(device,client,deviceMessageCodec);
            Consumer<DeviceSession> sessionConsumer = DeviceGatewayHelper.applySessionKeepaliveTimeout(message,keepaliveTimeout::get);
            sessionConsumer.andThen(session -> {
                TcpDeviceSession deviceSession = session.unwrap(TcpDeviceSession.class);
                deviceSession.setClient(client);
                sessionRef.set(deviceSession);
            });
            helper.handleDeviceMessage(message,sessionBuilder,sessionConsumer);
        }
    }

}
