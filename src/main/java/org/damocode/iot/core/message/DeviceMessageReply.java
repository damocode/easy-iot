package org.damocode.iot.core.message;

/**
 * @Description: 设备消息回复
 * @Author: zzg
 * @Date: 2021/10/7 11:41
 * @Version: 1.0.0
 */
public interface DeviceMessageReply extends DeviceMessage{

    //是否成功
    boolean isSuccess();

    //业务码,具体由设备定义
    String getCode();

    //错误消息
    String getMessage();

    //设置失败
    DeviceMessageReply error(Throwable err);

    //设置设备ID
    DeviceMessageReply deviceId(String deviceId);

    //设置成功
    DeviceMessageReply success();

    //设置业务码
    DeviceMessageReply code(String code);

    //设置消息
    DeviceMessageReply message(String message);

    //根据另外的消息填充对应属性
    DeviceMessageReply from(Message message);

    //设置消息ID
    DeviceMessageReply messageId(String messageId);

    //添加头
    @Override
    DeviceMessageReply addHeader(String header,Object value);

    @Override
    default <T> DeviceMessageReply addHeader(HeaderKey<T> header,T value) {
        addHeader(header.getKey(), value);
        return this;
    }

}
