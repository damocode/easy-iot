package org.damocode.iot.device.message;

import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;

/**
 * @Description: 设备数据服务
 * @Author: zzg
 * @Date: 2021/11/3 18:15
 * @Version: 1.0.0
 */
public interface IDeviceDataService {

    default void handleMessage(DeviceOperator deviceOperator, DeviceMessage deviceMessage) {
        String messageId = deviceMessage.getMessageId();
        if(StringUtils.isBlank(messageId)){
            return;
        }
        if(accept(messageId)){
            saveMessage(deviceMessage);
        }
    }

    void saveMessage(DeviceMessage deviceMessage);

    Boolean accept(String messageId);

}
