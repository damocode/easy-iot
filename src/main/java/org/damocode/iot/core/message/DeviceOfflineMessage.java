package org.damocode.iot.core.message;

/**
 * @Description: 设备离线消息
 * @Author: zzg
 * @Date: 2021/10/7 11:40
 * @Version: 1.0.0
 */
public class DeviceOfflineMessage extends CommonDeviceMessage {

    public MessageType getMessageType() {
        return MessageType.OFFLINE;
    }

}
