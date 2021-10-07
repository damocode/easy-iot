package org.damocode.iot.core.server.session;

/**
 * @Description: 支持持久化的Session
 * @Author: zzg
 * @Date: 2021/10/7 14:50
 * @Version: 1.0.0
 */
public interface PersistentSession extends DeviceSession {

    /**
     * @return 会话提供者
     */
    String getProvider();

}
