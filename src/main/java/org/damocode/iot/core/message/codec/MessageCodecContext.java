package org.damocode.iot.core.message.codec;

import org.damocode.iot.core.device.DeviceOperator;

/**
 * @Description: 消息上下文
 * @Author: zzg
 * @Date: 2021/10/7 14:45
 * @Version: 1.0.0
 */
public interface MessageCodecContext {

    /**
     * 获取当前上下文中到设备操作接口, 在tcp,http等场景下,此接口可能返回null
     * @return
     */
    DeviceOperator getDevice();

    default DeviceOperator getDevice(String deviceId) {
        return null;
    }
}
