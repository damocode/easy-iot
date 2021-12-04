package org.damocode.iot.core.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 认证结果
 * @Author: zzg
 * @Date: 2021/10/14 10:13
 * @Version: 1.0.0
 */
@Getter
@Setter
@ToString
public class AuthenticationResponse {

    private boolean success;

    private int code;

    private String message;

    private String deviceId;

    public static AuthenticationResponse success() {
        return success(null);
    }


    public static AuthenticationResponse success(String deviceId) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.success = true;
        response.code = 200;
        response.message = "授权通过";
        response.deviceId = deviceId;
        return response;
    }

    public static AuthenticationResponse error(int code, String message) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.success = false;
        response.code = code;
        response.message = message;
        return response;
    }

}
