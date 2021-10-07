package org.damocode.iot.core.device;

import org.damocode.iot.core.message.DeviceMessageReply;
import org.damocode.iot.core.message.Message;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 设备操作代理,用于管理集群间设备指令发送
 * @Author: zzg
 * @Date: 2021/10/7 11:46
 * @Version: 1.0.0
 */
public interface DeviceOperationBroker {

    /**
     * 根据消息ID监听响应
     * @param deviceId 设备Id
     * @param messageId 消息Id
     * @param timeout 超时时间
     * @return 消息返回
     */
    PublishSubject<DeviceMessageReply> handleReply(String deviceId, String messageId, Duration timeout);

    /**
     * 发送设备消息到指定到服务
     * @param serverId 设备所在服务ID
     * @param message
     */
    void send(String serverId, Message message);

    /**
     * 获取指定服务里设备状态
     * @param deviceGatewayServerId 设备所在服务ID
     * @param deviceIdList 设备列表
     * @return 设备状态
     */
    List<DeviceStateInfo> getDeviceState(String deviceGatewayServerId, Collection<String> deviceIdList);

}
