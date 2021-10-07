package org.damocode.iot.core.message.codec;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;

/**
 * @Description: 消息编码上下文
 * @Author: zzg
 * @Date: 2021/10/7 14:46
 * @Version: 1.0.0
 */
public interface MessageEncodeContext extends MessageCodecContext {

    /**
     * 获取平台下发的给设备的消息指令
     * @return
     */
    Message getMessage();

    void reply(DeviceMessage replyMessage);

    /**
     * 使用新的消息和设备，转换为新上下文
     * @param anotherMessage 设备消息
     * @param device 设备操作接口
     * @return 上下文
     */
    default MessageEncodeContext mutate(Message anotherMessage, DeviceOperator device) {
        return new MessageEncodeContext() {

            @Override
            public Message getMessage() {
                return anotherMessage;
            }

            @Override
            public DeviceOperator getDevice(String deviceId) {
                return MessageEncodeContext.this.getDevice(deviceId);
            }

            @Override
            public DeviceOperator getDevice() {
                return device;
            }

            @Override
            public void reply(DeviceMessage replyMessage) {
                MessageEncodeContext.this.reply(replyMessage);
            }
        };
    }


}

