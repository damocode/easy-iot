package org.damocode.iot.core.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 设备状态对象
 * @Author: zzg
 * @Date: 2021/10/7 11:47
 * @Version: 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStateInfo implements Serializable {
    private static final long serialVersionUID = 303335887964742286L;

    private String deviceId;
    private byte state;
}
