package org.damocode.iot.core.device;

import java.util.Date;

/**
 * @Description: 设备操作对象
 * @Author: zzg
 * @Date: 2021/10/7 11:46
 * @Version: 1.0.0
 */
public interface DeviceOperatorInfo {

    String getDeviceId();

    String getServerId();

    String getSessionId();

    String getAddress();

    Date getOnlineTime();

    Date getOfflineTime();

    byte getState();

    void setServerId(String serverId);

    void setSessionId(String sessionId);

    void setAddress(String address);

    void setOnlineTime(Date onlineTime);

    void setOfflineTime(Date offlineTime);

    void setState(byte state);

}
