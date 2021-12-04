package org.damocode.iot.network.mqtt.device;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.device.AuthenticationResponse;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.device.MqttAuthenticationRequest;
import org.damocode.iot.core.message.CommonDeviceMessage;
import org.damocode.iot.core.message.CommonDeviceMessageReply;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.message.codec.*;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.mqtt.MqttAuth;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.core.server.session.DeviceSessionManager;
import org.damocode.iot.core.server.session.ReplaceableDeviceSession;
import org.damocode.iot.network.mqtt.device.session.MqttConnectionSession;
import org.damocode.iot.network.mqtt.server.MqttConnection;
import org.damocode.iot.network.mqtt.server.MqttPublishing;
import org.damocode.iot.network.mqtt.server.MqttServer;
import org.damocode.iot.network.utils.DeviceGatewayHelper;
import org.springframework.util.StringUtils;
import rx.subjects.PublishSubject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: Mqtt服务设备网关
 * @Author: zzg
 * @Date: 2021/10/14 9:58
 * @Version: 1.0.0
 */
@Slf4j
public class MqttServerDeviceGateway {

    @Getter
    private final String id;

    private final DeviceSessionManager sessionManager;

    private final DeviceOperatorManager deviceOperatorManager;

    private final MqttServer mqttServer;

    private final ProtocolSupport protocolSupport;

    private final DecodedClientMessageHandler messageHandler;

    private final DeviceGatewayHelper helper;

    private PublishSubject<Message> messageProcessor = PublishSubject.create();

    private final AtomicBoolean started = new AtomicBoolean();

    public MqttServerDeviceGateway(String id,
                                   DeviceSessionManager sessionManager,
                                   DeviceOperatorManager deviceOperatorManager,
                                   MqttServer mqttServer,
                                   ProtocolSupport protocolSupport,
                                   DecodedClientMessageHandler clientMessageHandler) {
        this.id = id;
        this.sessionManager = sessionManager;
        this.deviceOperatorManager = deviceOperatorManager;
        this.mqttServer = mqttServer;
        this.protocolSupport = protocolSupport;
        this.messageHandler = clientMessageHandler;
        this.helper = new DeviceGatewayHelper(deviceOperatorManager, sessionManager, clientMessageHandler);
    }

    private void doStart() {
        if (started.getAndSet(true)) {
            return;
        }
        mqttServer.handleConnection().subscribe(conn -> {
            if (!started.get()) {
                conn.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
            }
            HandleConnectionResult connectionResult = this.handleConnection(conn);
            HandleAuthResponseResult authResponseResult = this.handleAuthResponse(connectionResult.getDeviceOperator(),connectionResult.getResponse(),connectionResult.getConnection());
            this.handleAcceptedMqttConnection(authResponseResult.getConnection(),authResponseResult.getDeviceOperator(),authResponseResult.getSession());
        });

    }

    public Transport getTransport() {
        return DefaultTransport.MQTT;
    }

    private HandleConnectionResult handleConnection(MqttConnection connection) {
        MqttAuth auth = connection.getAuth();
        MqttAuthenticationRequest request = new MqttAuthenticationRequest(connection.getClientId(), auth.getUsername(), auth.getPassword(),getTransport());
        AuthenticationResponse response = protocolSupport.authenticate(request);
        if(response == null){
            connection.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
            return null;
        }
        String deviceId = StringUtils.isEmpty(response.getDeviceId()) ? connection.getClientId() : response.getDeviceId();
        DeviceOperator operator = deviceOperatorManager.getDevice(deviceId);
        if(operator == null) {
            connection.reject(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            return null;
        }
        return new HandleConnectionResult(operator, response, connection);
    }

    private HandleAuthResponseResult handleAuthResponse(DeviceOperator device,AuthenticationResponse resp,MqttConnection connection) {
        String deviceId = device.getDeviceId();
        if (resp.isSuccess()) {
            DeviceSession session = sessionManager.getSession(deviceId);
            MqttConnectionSession newSession = new MqttConnectionSession(deviceId, device, protocolSupport, getTransport(), connection);
            if (null == session) {
                sessionManager.register(newSession);
            } else if (session instanceof ReplaceableDeviceSession) {
                ((ReplaceableDeviceSession) session).replaceWith(newSession);
            }
            //监听断开连接
            connection.onClose(conn -> {
                DeviceSession _tmp = sessionManager.getSession(newSession.getId());

                if (newSession == _tmp || _tmp == null) {
                    sessionManager.unregister(deviceId);
                }
            });
            return new HandleAuthResponseResult(connection.accept(), device, newSession);
        } else {
            log.warn("MQTT客户端认证[{}]失败:{}", deviceId, resp.getMessage());
            connection.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
        }
        return null;
    }

    private void handleAcceptedMqttConnection(MqttConnection connection, DeviceOperator operator, MqttConnectionSession session) {
        PublishSubject<MqttPublishing> publishSubject = connection.handleMessage();
        if(started.get()){
            connection.close();
        }
        publishSubject.subscribe(publishing -> {
            this.decodeAndHandleMessage(operator, session, publishing.getMessage(), connection);
            publishing.acknowledge();
        });
        connection.getWillMessage()
                .ifPresent(mqttMessage -> this.decodeAndHandleMessage(operator, session, mqttMessage, connection));
    }

    private void decodeAndHandleMessage(DeviceOperator operator, MqttConnectionSession session,MqttMessage message, MqttConnection connection) {
        DeviceMessageCodec codec = protocolSupport.getMessageCodec(getTransport());
        DeviceMessage msg = codec.decode(FromDeviceMessageContext.of(session, message));
        if (messageProcessor.hasObservers()) {
            messageProcessor.onNext(msg);
        }
        if (msg instanceof CommonDeviceMessage) {
            CommonDeviceMessage _msg = ((CommonDeviceMessage) msg);
            if (StringUtils.isEmpty(_msg.getDeviceId())) {
                _msg.setDeviceId(operator.getDeviceId());
            }
        }
        if (msg instanceof CommonDeviceMessageReply) {
            CommonDeviceMessageReply<?> _msg = ((CommonDeviceMessageReply<?>) msg);
            if (StringUtils.isEmpty(_msg.getDeviceId())) {
                _msg.setDeviceId(operator.getDeviceId());
            }
        }
        handleMessage(operator, msg, connection);
    }

    private void handleMessage(DeviceOperator mainDevice,
                               DeviceMessage message,
                               MqttConnection connection) {
        if (!connection.isAlive()) {
            messageHandler.handleMessage(mainDevice, message);
            return;
        }
        helper.handleDeviceMessage(message,
                device -> new MqttConnectionSession(device.getDeviceId(),device, protocolSupport, getTransport(),connection),
                session ->  {
                });
    }

    public void startup() {
        doStart();
    }

    @Data
    @AllArgsConstructor
    class HandleConnectionResult {
        private DeviceOperator deviceOperator;
        private AuthenticationResponse response;
        private MqttConnection connection;
    }

    @Data
    @AllArgsConstructor
    class HandleAuthResponseResult {
        private MqttConnection connection;
        private DeviceOperator deviceOperator;
        private MqttConnectionSession session;
    }

}
