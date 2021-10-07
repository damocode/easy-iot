package org.damocode.iot.core.device;

import org.damocode.iot.core.message.DeviceMessage;

/**
 * @Description: 设备消息发送器
 * @Author: zzg
 * @Date: 2021/10/7 11:33
 * @Version: 1.0.0
 */
public interface DeviceMessageSender {

    Boolean send(DeviceMessage message);

}
