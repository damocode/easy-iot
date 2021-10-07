package org.damocode.iot.core.message.codec;

import org.damocode.iot.core.server.session.DeviceSession;

/**
 * @Description: 设备消息上下文
 * @Author: zzg
 * @Date: 2021/10/7 14:47
 * @Version: 1.0.0
 */
public interface FromDeviceMessageContext extends MessageDecodeContext {

    DeviceSession getSession();

    static FromDeviceMessageContext of(final DeviceSession session, final EncodedMessage message){
        return new FromDeviceMessageContext() {
            @Override
            public DeviceSession getSession() {
                return session;
            }

            @Override
            public EncodedMessage getMessage() {
                return message;
            }
        };
    }

}
