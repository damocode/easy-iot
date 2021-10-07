package org.damocode.iot.core.message;

/**
 * @Description: 设备上线消息
 * @Author: zzg
 * @Date: 2021/10/7 11:41
 * @Version: 1.0.0
 */
public class DeviceOnlineMessage extends CommonDeviceMessage{

    public MessageType getMessageType() {
        return MessageType.ONLINE;
    }

}

