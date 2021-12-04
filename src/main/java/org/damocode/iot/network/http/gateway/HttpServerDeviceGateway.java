package org.damocode.iot.network.http.gateway;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.message.codec.DefaultTransport;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.FromDeviceMessageContext;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.session.DeviceSessionManager;
import org.damocode.iot.core.topic.Router;
import org.damocode.iot.core.utils.SystemUtils;
import org.damocode.iot.network.http.server.HttpExchange;
import org.damocode.iot.network.http.server.HttpServer;
import org.damocode.iot.network.utils.DeviceGatewayHelper;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import rx.subjects.PublishSubject;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @Description: Http服务设备网关
 * @Author: zzg
 * @Date: 2021/10/26 11:44
 * @Version: 1.0.0
 */
@Slf4j
public class HttpServerDeviceGateway {

    @Getter
    private final String id;

    private final HttpServer httpServer;

    private final Function<String, ProtocolSupport> protocolSupplier;

    private final PublishSubject<Message> subject = PublishSubject.create();

    private final AtomicReference<Boolean> started = new AtomicReference<>(false);

    private final String[] urls;

    private final DeviceGatewayHelper helper;

    private PublishSubject<HttpExchange> pub;

    public HttpServerDeviceGateway(String id,
                                   HttpServer server,
                                   DeviceSessionManager sessionManager,
                                   DeviceOperatorManager deviceOperatorManager,
                                   Map<String, ProtocolSupport> protocolSupportMap,
                                   DecodedClientMessageHandler clientMessageHandler) {
        this.id = id;
        this.httpServer = server;
        this.helper = new DeviceGatewayHelper(deviceOperatorManager, sessionManager, clientMessageHandler);
        //根据url路由获取协议包
        Router<String, ProtocolSupport> router = Router.create();
        List<String> urls = new ArrayList<>();
        for(Map.Entry<String,ProtocolSupport> entry : protocolSupportMap.entrySet()){
            String url = entry.getKey();
            if (StringUtils.isEmpty(url)) {
                url = "/**";
            }
            urls.add(url);
            router.route(url, ignore -> () -> entry.getValue());
        }
        this.protocolSupplier = url -> router.execute(url,url).stream().findFirst().get().get();
        this.urls = urls.toArray(new String[0]);
    }

    public Transport getTransport() {
        return DefaultTransport.HTTP;
    }

    public PublishSubject<Message> onMessage() {
        return subject;
    }

    public void startup() {
        this.doStart();
    }

    public void shutdown() {
        started.set(false);
        if(pub != null  && !pub.hasCompleted()){
            pub.onCompleted();
        }
        pub = null;
    }

    public void pause() {
        started.set(false);
    }

    public boolean isAlive() {
        return started.get();
    }

    private void doStart() {
        if (started.getAndSet(true) || pub != null) {
            return;
        }
        pub = httpServer.handleRequest("*", exchange -> {
            if (!started.get()) {
                exchange.error(HttpStatus.BAD_GATEWAY);
                return;
            }
            ProtocolSupport protocol = protocolSupplier.apply(exchange.request().getUrl());
            exchange.toExchangeMessage(httpMessage -> {
                if (log.isDebugEnabled()) {
                    log.debug("收到HTTP请求\n{}", httpMessage);
                }
                InetSocketAddress address = exchange.request().getClientAddress();
                if (SystemUtils.memoryIsOutOfWaterline()) {
                    log.warn("memory out of water line,discard http[{}] request [{}].", address, exchange
                            .request()
                            .getUrl());
                    exchange.error(HttpStatus.SERVICE_UNAVAILABLE);
                    return;
                }
                AtomicReference<Duration> timeoutRef = new AtomicReference<>(Duration.ofMinutes(30));
                DeviceMessageCodec codec = protocol.getMessageCodec(getTransport());
                DeviceMessage deviceMessage = codec.decode(FromDeviceMessageContext.of(new UnknownHttpDeviceSession(protocol){
                    @Override
                    public void setKeepAliveTimeout(Duration timeout) {
                        timeoutRef.set(timeout);
                    }

                    @Override
                    public Optional<InetSocketAddress> getClientAddress() {
                        return Optional.of(address);
                    }
                },httpMessage));
                if(deviceMessage != null){
                    if(subject.hasObservers()){
                        subject.onNext(deviceMessage);
                    }
                    helper.handleDeviceMessage(deviceMessage,device -> new HttpDeviceSession(device, address, protocol),DeviceGatewayHelper.applySessionKeepaliveTimeout(deviceMessage,timeoutRef::get));
                    if (!exchange.isClosed()) {
                        exchange.ok();
                    }
                }
            });
        },urls);
    }

}
