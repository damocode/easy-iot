package org.damocode.iot.core.device;

import org.damocode.iot.core.message.codec.Transport;

import java.io.Serializable;

/**
 * @Description: 认证请求参数
 * @Author: zzg
 * @Date: 2021/10/14 11:24
 * @Version: 1.0.0
 */
public interface AuthenticationRequest extends Serializable {

    Transport getTransport();

}
