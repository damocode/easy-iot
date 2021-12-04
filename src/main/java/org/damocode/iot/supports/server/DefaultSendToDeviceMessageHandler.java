package org.damocode.iot.supports.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceState;
import org.damocode.iot.core.device.DeviceStateInfo;
import org.damocode.iot.core.message.*;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.EncodedMessage;
import org.damocode.iot.core.message.codec.ToDeviceMessageContext;
import org.damocode.iot.core.protocol.ProtocolSupport;
import org.damocode.iot.core.server.DecodedClientMessageHandler;
import org.damocode.iot.core.server.MessageHandler;
import org.damocode.iot.core.server.session.DeviceSession;
import org.damocode.iot.core.server.session.DeviceSessionManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 默认设备消息发送处理器
 * @Author: zzg
 * @Date: 2021/10/7 14:52
 * @Version: 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class DefaultSendToDeviceMessageHandler {

    private final String serverId;

    private final DeviceSessionManager sessionManager;

    private final MessageHandler handler;

    private final DecodedClientMessageHandler decodedClientMessageHandler;

    public void startup() {
        //处理发往设备的消息
        handler.handleSendToDeviceMessage(serverId)
                .subscribe(message -> {
                    try {
                        if (message instanceof DeviceMessage) {
                            handleDeviceMessage(((DeviceMessage) message));
                        }
                    } catch (Throwable e) {
                        log.error("handle send to device message error {}", message, e);
                    }
                });
        handler.handleGetDeviceState(serverId, id -> new DeviceStateInfo(id, sessionManager.sessionIsAlive(id) ? DeviceState.online : DeviceState.offline));
    }

    protected void handleDeviceMessage(DeviceMessage message) {
        String deviceId = message.getDeviceId();
        DeviceSession session = sessionManager.getSession(deviceId);
        if(session != null){
            doSend(message, session);
        }
    }

    protected void doSend(DeviceMessage message, DeviceSession session) {
        String deviceId = message.getDeviceId();
        DeviceMessageReply reply = this.createReply(deviceId, message);
        AtomicBoolean alreadyReply = new AtomicBoolean(false);
        if (session.getOperator() == null) {
            log.warn("unsupported send message to {}", session);
            return;
        }
        DeviceSession fSession = session.unwrap(DeviceSession.class);
        boolean forget = message.getHeader(Headers.sendAndForget).orElse(false);
        DeviceMessageCodec codec = fSession.getDeviceMessageCodec();
        EncodedMessage encodedMessage = codec.encode(new ToDeviceMessageContext() {

            @Override
            public Boolean sendToDevice(EncodedMessage message) {
                return fSession.send(message);
            }

            @Override
            public void disconnect() {
                fSession.close();
                sessionManager.unregister(fSession.getId());
            }

            @Override
            public DeviceSession getSession() {
                return fSession;
            }

            @Override
            public DeviceSession getSession(String deviceId) {
                return sessionManager.getSession(deviceId);
            }

            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public DeviceOperator getDevice() {
                return fSession.getOperator();
            }

            public void reply(DeviceMessage replyMessage) {
                alreadyReply.set(true);
                decodedClientMessageHandler.handleMessage(fSession.getOperator(), replyMessage);
            }
        });
        session.send(encodedMessage);
    }

    protected DeviceMessageReply createReply(String deviceId, DeviceMessage message) {
        DeviceMessageReply reply;
        if (message instanceof RepayableDeviceMessage) {
            reply = ((RepayableDeviceMessage<?>) message).newReply();
        } else {
            reply = new AcknowledgeDeviceMessage();
        }
        reply.messageId(message.getMessageId()).deviceId(deviceId);
        return reply;
    }

}
