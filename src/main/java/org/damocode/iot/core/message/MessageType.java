package org.damocode.iot.core.message;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

/**
 * @Description: 消息类型
 * @Author: zzg
 * @Date: 2021/10/7 11:34
 * @Version: 1.0.0
 */
@AllArgsConstructor
public enum MessageType {

    //设备离线
    OFFLINE(DeviceOfflineMessage::new),

    //注册
    REGISTER(DeviceRegisterMessage::new),

    //设备上线
    ONLINE(DeviceOnlineMessage::new),

    //断开回复
    DISCONNECT_REPLY(DisconnectDeviceMessageReply::new),

    //平台主动断开连接
    DISCONNECT(DisconnectDeviceMessage::new),

    //应答指令
    ACKNOWLEDGE(AcknowledgeDeviceMessage::new),

    UNKNOWN(null);

    Supplier<? extends Message> newInstance;

}
