package org.damocode.iot.core.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.damocode.iot.core.message.codec.Transport;

/**
 * @Description: Mqtt认证请求参数
 * @Author: zzg
 * @Date: 2021/10/14 10:37
 * @Version: 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MqttAuthenticationRequest implements AuthenticationRequest{

    private String clientId;

    private String username;

    private String password;

    private Transport transport;

}
