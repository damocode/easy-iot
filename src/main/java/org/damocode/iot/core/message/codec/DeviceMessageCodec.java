package org.damocode.iot.core.message.codec;

/**
 * @Description: 设备消息转换器,用于对不同协议的消息进行转换
 * @Author: zzg
 * @Date: 2021/10/7 14:48
 * @Version: 1.0.0
 */
public interface DeviceMessageCodec extends DeviceMessageEncoder, DeviceMessageDecoder {

    Transport getSupportTransport();

}

