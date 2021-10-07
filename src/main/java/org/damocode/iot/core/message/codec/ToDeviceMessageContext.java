package org.damocode.iot.core.message.codec;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.DeviceMessage;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.server.session.DeviceSession;

/**
 * @Description: 发送给设备的上下文,在设备已经在平台中建立会话后,平台下发的指令都会使用此上下文接口
 * @Author: zzg
 * @Date: 2021/10/7 14:44
 * @Version: 1.0.0
 */
public interface ToDeviceMessageContext extends MessageEncodeContext {

    /**
     * 直接发送消息给设备
     * @param message 消息
     * @return 是否成功
     */
    Boolean sendToDevice(EncodedMessage message);

    /**
     * 断开设备与平台的连接
     */
    void disconnect();

    /**
     * 获取设备会话
     * @return
     */
    DeviceSession getSession();

    /**
     * 获取指定设备的会话
     * @param deviceId 设备ID
     * @return
     */
    DeviceSession getSession(String deviceId);

    /**
     * 使用新的消息和设备，转换为新上下文. 通常用于在网关设备协议中,调用子设备协议时.通过此方法将上下为变换为对子设备对操作上下文.
     * @param anotherMessage 设备消息
     * @param device 设备操作接口
     * @return 上下文
     */
    default ToDeviceMessageContext mutate(Message anotherMessage, DeviceOperator device) {
        return new ToDeviceMessageContext() {
            @Override
            public Boolean sendToDevice(EncodedMessage message) {
                return ToDeviceMessageContext.this.sendToDevice(message);
            }

            @Override
            public void disconnect() {
                ToDeviceMessageContext.this.disconnect();
            }

            @Override
            public DeviceSession getSession() {
                return ToDeviceMessageContext.this.getSession();
            }

            @Override
            public DeviceSession getSession(String deviceId) {
                return ToDeviceMessageContext.this.getSession(deviceId);
            }

            @Override
            public Message getMessage() {
                return anotherMessage;
            }

            @Override
            public DeviceOperator getDevice(String deviceId) {
                return ToDeviceMessageContext.this.getDevice(deviceId);
            }

            @Override
            public DeviceOperator getDevice() {
                return device;
            }

            @Override
            public void reply(DeviceMessage replyMessage) {
                ToDeviceMessageContext.this.reply(replyMessage);
            }
        };
    }

}
