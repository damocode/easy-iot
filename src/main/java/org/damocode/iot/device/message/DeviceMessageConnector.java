package org.damocode.iot.device.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.server.DecodedClientMessageHandler;

import java.util.List;

/**
 * @Description: 处理设备消息
 * @Author: zzg
 * @Date: 2021/11/3 17:26
 * @Version: 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DeviceMessageConnector implements DecodedClientMessageHandler {

    private final List<IDeviceDataService> deviceDataServices;

    @Override
    public Boolean handleMessage(DeviceOperator device, Message message) {
        if (message instanceof DeviceMessage) {
            DeviceMessage deviceMessage = ((DeviceMessage) message);
            String deviceId = deviceMessage.getDeviceId();
            if (StringUtils.isEmpty(deviceId)) {
                log.warn("无法从消息中获取设备ID:{}", deviceMessage);
                return false;
            }
            if(deviceDataServices == null || deviceDataServices.size() == 0){
                return false;
            }
            for(IDeviceDataService deviceDataService: deviceDataServices){
                deviceDataService.handleMessage(device,deviceMessage);
            }
        }
        return true;
    }

}
