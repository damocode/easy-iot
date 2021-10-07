package org.damocode.iot.core.server.session;

/**
 * @Description: 可替换的设备会话
 * @Author: zzg
 * @Date: 2021/10/7 14:50
 * @Version: 1.0.0
 */
public interface ReplaceableDeviceSession {

    void replaceWith(DeviceSession session);

}
