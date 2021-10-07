package org.damocode.iot.core.server.session;

import rx.subjects.PublishSubject;

import java.util.List;

/**
 * @Description: 设备会话管理器,用于管理所有设备连接会话
 * @Author: zzg
 * @Date: 2021/10/7 14:49
 * @Version: 1.0.0
 */
public interface DeviceSessionManager {

    /**
     * 根据设备ID或者会话ID获取设备会话
     *
     * @param idOrDeviceId 设备ID或者会话ID
     * @return 设备会话, 不存在则返回null
     */
    DeviceSession getSession(String idOrDeviceId);

    /**
     * 注册新到设备会话,如果已经存在相同设备ID到会话,将注销旧的会话.
     * @param session 新的设备会话
     * @return 旧的设备会话, 不存在则返回null
     */
    DeviceSession register(DeviceSession session);

    PublishSubject<DeviceSession> onRegister();

    PublishSubject<DeviceSession> onUnRegister();

    /**
     * 替换session
     * @param oldSession 旧session
     * @param newSession 新session
     * @return 新session
     */
    default DeviceSession replace(DeviceSession oldSession, DeviceSession newSession){
        return newSession;
    }

    /**
     * 使用会话ID或者设备ID注销设备会话
     * @param idOrDeviceId 设备ID或者会话ID
     * @return 被注销的会话, 不存在则返回null
     */
    DeviceSession unregister(String idOrDeviceId);

    boolean sessionIsAlive(String deviceId);

    List<DeviceSession> getAllSession();

}
