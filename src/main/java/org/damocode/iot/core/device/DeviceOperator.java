package org.damocode.iot.core.device;

import java.util.Optional;

/**
 * @Description: 设备操作接口
 * @Author: zzg
 * @Date: 2021/10/7 11:30
 * @Version: 1.0.0
 */
public interface DeviceOperator {

    /**
     * 设备ID
     * @return
     */
    String getDeviceId();

    /**
     * 当前设备连接所在服务器ID，如果设备未上线,则返回null
     * @return
     */
    String getConnectionServerId();

    /**
     * 当前设备连接会话ID
     *
     * @return
     */
    String getSessionId();

    /**
     * 获取设备地址,通常是ip地址.
     * @return 地址
     */
    String getAddress();

    /**
     * 设置当前状态
     * @param state 状态
     * @return
     */
    Boolean putState(byte state);

    /**
     * 获取当前状态
     * @return
     */
    Byte getState();

    /**
     * 检查设备的真实状态
     * @return
     */
    Byte checkState();

    default Boolean online(String serverId, String sessionId) {
        return online(serverId, sessionId, null);
    }

    Boolean online(String serverId, String sessionId, String address);

    /**
     * 设置设备离线
     * @return
     */
    Boolean offline();

    /**
     * 断开设备连接
     * @return 断开结果
     */
    Boolean disconnect();

    /**
     * @return 是否在线
     */
    default Boolean isOnline() {
        return Optional.ofNullable(checkState())
                .map(state -> state.equals(DeviceState.online))
                .orElse(false);
    }

    /**
     * 消息发送器, 用于发送消息给设备
     * @return
     */
    DeviceMessageSender messageSender();

}
