package org.damocode.iot.core.device;

/**
 * @Description: 设备状态检查器,用于自定义设备状态检查
 * @Author: zzg
 * @Date: 2021/10/7 11:32
 * @Version: 1.0.0
 */
public interface DeviceStateChecker {

    /**
     * 检查设备状态
     * @param device 设备操作接口
     * @return 设备状态
     */
    Byte checkState(DeviceOperator device);

    /**
     * 排序需要，值越小优先级越高
     * @return 序号
     */
    default long order() {
        return Long.MAX_VALUE;
    }

}
