package org.damocode.iot.core.message.codec;

/**
 * @Description: 设备消息编码器,用于将消息对象编码为对应消息协议的消息
 * @Author: zzg
 * @Date: 2021/10/7 14:48
 * @Version: 1.0.0
 */
public interface DeviceMessageEncoder {

    EncodedMessage encode(MessageEncodeContext context);

}

