package org.damocode.iot.core.device;

/**
 * @Description: 设备操作接口
 * @Author: zzg
 * @Date: 2021/10/7 11:47
 * @Version: 1.0.0
 */
public interface IDeviceOperatorService {

    DeviceOperatorInfo getByDeviceId(String deviceId);

    <T extends DeviceOperatorInfo> Boolean updateByDeviceId(T operatorInfo);

}

