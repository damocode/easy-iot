package org.damocode.iot.core.message.codec;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @Description: 传输协议定义,如: TCP,MQTT等,通常使用枚举来定义
 * @Author: zzg
 * @Date: 2021/10/14 11:16
 * @Version: 1.0.0
 */
public interface Transport {

    /**
     * @return 唯一标识
     */
    String getId();

    /**
     * @return 名称，默认和ID一致
     */
    default String getName() {
        return getId();
    }

    /**
     * @return 描述
     */
    default String getDescription() {
        return null;
    }

    /**
     * 比较与指定等协议是否为同一种协议
     * @param transport
     * @return
     */
    default boolean isSame(Transport transport) {
        return this == transport || this.getId().equals(transport.getId());
    }

    /**
     * 使用ID进行对比，判断是否为同一个协议
     * @param transportId
     * @return
     */
    default boolean isSame(String transportId) {
        return this.getId().equals(transportId);
    }


    /**
     * 使用指定的ID来创建协议定义
     * @param id
     * @return
     */
    static Transport of(String id) {
        return lookup(id).orElseGet(() -> (Transport & Serializable) () -> id);
    }

    /**
     * 通过ID查找协议定义
     * @param id
     * @return
     */
    static Optional<Transport> lookup(String id) {
        return Transports.lookup(id);
    }

    /**
     * 获取全部协议定义
     * @return
     */
    static List<Transport> getAll() {
        return Transports.get();
    }

}
