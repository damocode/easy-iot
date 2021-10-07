package org.damocode.iot.core.server;

import org.damocode.iot.core.device.DeviceStateInfo;
import org.damocode.iot.core.message.DeviceMessageReply;
import org.damocode.iot.core.message.Message;
import rx.subjects.PublishSubject;

import java.util.function.Function;

/**
 * @Description: 消息处理器,在服务启动后,用于接收来着平台的指令并进行相应的处理
 * @Author: zzg
 * @Date: 2021/10/7 11:58
 * @Version: 1.0.0
 */
public interface MessageHandler {

    /**
     * 监听发往设备的指令
     * @param serverId 服务ID,在集群时,不同的节点serverId不同
     * @return 发往设备的消息指令流
     */
    PublishSubject<Message> handleSendToDeviceMessage(String serverId);

    /**
     * 监听获取设备真实状态请求,并响应状态结果
     * @param serverId 服务ID,在集群时,不同的节点serverId不同
     * @param stateMapper 状态检查器
     */
    void handleGetDeviceState(String serverId, Function<String, DeviceStateInfo> stateMapper);

    /**
     * 回复平台下发的指令
     * @param message 回复指令
     * @return
     */
    Boolean reply(DeviceMessageReply message);

}
