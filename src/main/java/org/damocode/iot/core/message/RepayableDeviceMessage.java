package org.damocode.iot.core.message;

/**
 * @Author: zzg
 * @Date: 2021/10/7 11:44
 * @Version: 1.0.0
 */
public interface RepayableDeviceMessage <R extends DeviceMessageReply> extends DeviceMessage {

    /**
     * 新建一个回复对象
     * @return 回复对象
     */
    R newReply();
}

