package org.damocode.iot.core.defaults;

import org.damocode.iot.core.device.AuthenticationRequest;
import org.damocode.iot.core.device.AuthenticationResponse;
import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceOperatorManager;

/**
 * @Description: 认证器,用于设备连接的时候进行认证
 * @Author: zzg
 * @Date: 2021/10/14 11:35
 * @Version: 1.0.0
 */
public interface Authenticator {

    /**
     * 对指定对设备进行认证
     * @param request 认证请求
     * @return 认证结果
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

}
