package org.damocode.iot.core.server;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.message.Message;

/**
 * @Description: 客户端消息解码器
 * @Author: zzg
 * @Date: 2021/10/7 14:39
 * @Version: 1.0.0
 */
public interface DecodedClientMessageHandler {

    Boolean handleMessage(DeviceOperator device, Message message);

}
