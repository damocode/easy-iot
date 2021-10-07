package org.damocode.iot.core.defaults;

import org.damocode.iot.core.device.DeviceMessageSender;
import org.damocode.iot.core.device.DeviceOperationBroker;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;

import java.util.Optional;

/**
 * @Description: 默认设备消息发送器
 * @Author: zzg
 * @Date: 2021/10/7 11:48
 * @Version: 1.0.0
 */
public class DefaultDeviceMessageSender implements DeviceMessageSender {

    private final DeviceOperationBroker handler;

    private final DeviceOperator operator;

    public DefaultDeviceMessageSender(DeviceOperationBroker handler,DeviceOperator operator){
        this.handler = handler;
        this.operator = operator;
    }

    @Override
    public Boolean send(DeviceMessage message) {
        String serverId = Optional.ofNullable(operator.getConnectionServerId()).orElse("");
        handler.send(serverId,message);
        return true;
    }

}
