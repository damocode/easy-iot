package org.damocode.iot.core.message;

/**
 * @Description: 应答消息,通常用于发送非可回复消息后的应答
 * @Author: zzg
 * @Date: 2021/10/7 11:45
 * @Version: 1.0.0
 */
public class AcknowledgeDeviceMessage extends CommonDeviceMessageReply<AcknowledgeDeviceMessage> {

    @Override
    public MessageType getMessageType() {
        return MessageType.ACKNOWLEDGE;
    }
}

