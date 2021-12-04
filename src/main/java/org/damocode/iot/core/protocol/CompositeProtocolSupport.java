package org.damocode.iot.core.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.damocode.iot.core.defaults.Authenticator;
import org.damocode.iot.core.device.AuthenticationRequest;
import org.damocode.iot.core.device.AuthenticationResponse;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.server.ClientConnection;
import org.damocode.iot.core.server.DeviceGatewayContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Description: 组合协议支持
 * @Author: zzg
 * @Date: 2021/10/14 11:31
 * @Version: 1.0.0
 */
@Getter
@Setter
public class CompositeProtocolSupport implements ProtocolSupport {

    private String id;

    private String name;

    private String description;

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Supplier<DeviceMessageCodec>> messageCodecSupports = new ConcurrentHashMap<>();

    @Getter(AccessLevel.PRIVATE)
    private Map<String, Authenticator> authenticators = new ConcurrentHashMap<>();

    private Map<String, BiFunction<ClientConnection, DeviceGatewayContext,Void>> connectionHandlers = new ConcurrentHashMap<>();

    private int order = Integer.MAX_VALUE;

    public void addMessageCodecSupport(Transport transport, Supplier<DeviceMessageCodec> supplier) {
        messageCodecSupports.put(transport.getId(), supplier);
    }

    public void addMessageCodecSupport(Transport transport, DeviceMessageCodec codec) {
        messageCodecSupports.put(transport.getId(), () -> codec);
    }

    public void addMessageCodecSupport(DeviceMessageCodec codec) {
        addMessageCodecSupport(codec.getSupportTransport(), codec);
    }

    public void addAuthenticator(Transport transport, Authenticator authenticator) {
        authenticators.put(transport.getId(), authenticator);
    }

    @Override
    public List<? extends Transport> getSupportedTransport() {
        return messageCodecSupports.values()
                .stream().map(Supplier::get)
                .map(DeviceMessageCodec::getSupportTransport)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceMessageCodec getMessageCodec(Transport transport) {
        return messageCodecSupports.get(transport.getId()).get();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authenticator authenticator = getAuthenticator(request);
        AuthenticationResponse response = authenticator.authenticate(request);
        if(response == null){
            return AuthenticationResponse.error(400, "无法获取认证结果");
        }
        return response;
    }

    /**
     * 监听客户端连接,只有部分协议支持此操作
     * @param transport 通信协议
     * @param handler 处理器
     */
    public void doOnClientConnect(Transport transport,BiFunction<ClientConnection, DeviceGatewayContext, Void> handler) {
        connectionHandlers.put(transport.getId(), handler);
    }

    @Override
    public void onClientConnect(Transport transport,ClientConnection connection,DeviceGatewayContext context) {
        BiFunction<ClientConnection, DeviceGatewayContext,Void> function = connectionHandlers.get(transport.getId());
        if (function == null) {
            return;
        }
        function.apply(connection, context);
    }

    private Authenticator getAuthenticator(AuthenticationRequest request) {
        Authenticator authenticator = authenticators.get(request.getTransport().getId());
        if(authenticator == null){
            throw new UnsupportedOperationException("不支持的认证请求:" + request);
        }
        return authenticator;
    }
}
