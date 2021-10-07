package org.damocode.iot.network;

/**
 * @Description: 网络组件
 * @Author: zzg
 * @Date: 2021/10/7 14:55
 * @Version: 1.0.0
 */
public interface Network {

    /**
     * ID唯一标识
     *
     * @return ID
     */
    String getId();

    /**
     * @return 是否存活
     */
    boolean isAlive();

    /**
     * 关闭网络组件
     */
    void shutdown();

}
