package org.damocode.iot.core.message.codec;

import org.damocode.iot.core.message.DeviceMessage;

/**
 * @Description: 设备消息解码器，用于将收到设备上传的消息解码为可读的消息。
 * @Author: zzg
 * @Date: 2021/10/7 14:48
 * @Version: 1.0.0
 */
public interface DeviceMessageDecoder {

    /**
     * 在服务器收到设备或者网络组件中发来的消息时，会调用协议包中的此方法来进行解码，将数据
     * 转为平台统一的消息
     *
     * @param context 消息上下文
     * @return 解码结果
     */
    DeviceMessage decode(MessageDecodeContext context);
}
