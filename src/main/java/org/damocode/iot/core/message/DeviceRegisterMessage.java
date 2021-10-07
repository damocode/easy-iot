package org.damocode.iot.core.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 设备注册消息,通常用于子设备连接,并自动与父设备进行绑定
 * @Author: zzg
 * @Date: 2021/10/7 11:41
 * @Version: 1.0.0
 */
@Getter
@Setter
public class DeviceRegisterMessage extends CommonDeviceMessage {

    @Override
    public MessageType getMessageType() {
        return MessageType.REGISTER;
    }
}
