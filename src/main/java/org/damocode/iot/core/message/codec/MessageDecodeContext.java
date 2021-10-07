package org.damocode.iot.core.message.codec;

/**
 * @Description: 消息解码上下文
 * @Author: zzg
 * @Date: 2021/10/7 14:47
 * @Version: 1.0.0
 */
public interface MessageDecodeContext {

    /**
     * 获取设备上报的原始消息,根据通信协议的不同,消息类型也不同, 在使用时可能需要转换为对应的消息类型
     *
     * @return 原始消息
     */
    EncodedMessage getMessage();

}
