package org.damocode.iot.core.message;

/**
 * @Description: 平台主动断开连接消息
 * @Author: zzg
 * @Date: 2021/10/7 11:43
 * @Version: 1.0.0
 */
public class DisconnectDeviceMessage extends CommonDeviceMessage implements RepayableDeviceMessage<DisconnectDeviceMessageReply> {

    @Override
    public DisconnectDeviceMessageReply newReply() {
        return new DisconnectDeviceMessageReply().from(this);
    }

    public MessageType getMessageType() {
        return MessageType.DISCONNECT;
    }

}
