package org.damocode.iot.network.utils;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;
import org.damocode.iot.core.message.*;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.core.server.session.DeviceSessionManager;
import org.damocode.iot.core.server.session.KeepOnlineSession;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description: 设备网关助手
 * @Author: zzg
 * @Date: 2021/10/7 15:06
 * @Version: 1.0.0
 */
@AllArgsConstructor
public class DeviceGatewayHelper {

    private final DeviceOperatorManager deviceOperatorManager;
    private final DeviceSessionManager sessionManager;
    private final DecodedClientMessageHandler messageHandler;

    public static Consumer<DeviceSession> applySessionKeepaliveTimeout(DeviceMessage msg, Supplier<Duration> timeoutSupplier) {
        return session -> {
            Duration timeout = msg
                    .getHeader(Headers.keepOnlineTimeoutSeconds)
                    .map(Duration::ofSeconds)
                    .orElseGet(timeoutSupplier);
            if (null != timeout) {
                session.setKeepAliveTimeout(timeout);
            }
        };
    }

    public DeviceOperator handleDeviceMessage(DeviceMessage message, Function<DeviceOperator,DeviceSession> sessionBuilder, Consumer<DeviceSession> sessionConsumer) {
        String deviceId = message.getDeviceId();
        boolean doHandle = true;
        if (StringUtils.isEmpty(deviceId)) {
            return null;
        }
        if (message instanceof DeviceOfflineMessage) {
            //设备离线消息
            DeviceSession session = sessionManager.unregister(deviceId);
            DeviceOperator operator = deviceOperatorManager.getDevice(deviceId);
            if(session == null){
                //如果session不存在,则将离线消息转发
                messageHandler.handleMessage(operator,message);
            }
            return operator;
        } else if (message instanceof DeviceOnlineMessage){
            doHandle = false;
        }
        DeviceSession session = sessionManager.getSession(deviceId);
        //session不存在,可能是同一个连接返回多个设备消息
        if (session == null) {
            DeviceOperator deviceOperator = deviceOperatorManager.getDevice(deviceId);
            DeviceSession newSession = sessionBuilder.apply(deviceOperator);
            if (null != newSession) {
                //保持会话，在低功率设备上,可能无法保持mqtt长连接.
                if (message.getHeader(Headers.keepOnline).orElse(false)) {
                    int timeout = message.getHeaderOrDefault(Headers.keepOnlineTimeoutSeconds);
                    newSession = new KeepOnlineSession(newSession, Duration.ofSeconds(timeout));
                }
                sessionManager.register(newSession);
                sessionConsumer.accept(newSession);
                newSession.keepAlive();
                if (!(message instanceof DeviceRegisterMessage) && !(message instanceof DeviceOnlineMessage)) {
                    messageHandler.handleMessage(deviceOperator,message);
                }
            }
            return deviceOperator;
        } else {
            //消息中指定保存在线
            if (message.getHeader(Headers.keepOnline).orElse(false) && !(session instanceof KeepOnlineSession)) {
                Duration timeout = message
                        .getHeader(Headers.keepOnlineTimeoutSeconds)
                        .map(Duration::ofSeconds)
                        .orElse(Duration.ofSeconds(2 * 60));
                //替换session
                session = sessionManager.replace(session, new KeepOnlineSession(session, timeout));
            }
            sessionConsumer.accept(session);
            session.keepAlive();
            if(doHandle){
                messageHandler.handleMessage(session.getOperator(), message);
            }
            return deviceOperatorManager.getDevice(deviceId);
        }
    }

}
