package org.damocode.iot.core.message;

/**
 * @Description: 设备断开回复消息
 * @Author: zzg
 * @Date: 2021/10/7 11:42
 * @Version: 1.0.0
 */
public class DisconnectDeviceMessageReply extends CommonDeviceMessageReply<DisconnectDeviceMessageReply> {

    public MessageType getMessageType() {
        return MessageType.DISCONNECT_REPLY;
    }

}
