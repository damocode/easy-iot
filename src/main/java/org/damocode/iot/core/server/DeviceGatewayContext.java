package org.damocode.iot.core.server;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;

/**
 * @Description: 设备网关上下文
 * @Author: zzg
 * @Date: 2021/10/14 15:27
 * @Version: 1.0.0
 */
public interface DeviceGatewayContext {

    /**
     * 根据ID获取设备操作接口
     * @param deviceId 设备ID
     * @return 设备操作接口
     */
    DeviceOperator getDevice(String deviceId);

    /**
     * 发送设备消息到设备网关,由平台统一处理这个消息.
     * @param message 设备消息
     */
    void onMessage(DeviceMessage message);

}
