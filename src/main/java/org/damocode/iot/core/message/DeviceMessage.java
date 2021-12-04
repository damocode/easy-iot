package org.damocode.iot.core.message;

/**
 * @Description: 设备消息
 * @Author: zzg
 * @Date: 2021/10/7 11:38
 * @Version: 1.0.0
 */
public interface DeviceMessage extends Message {

    String getDeviceId();

    long getTimestamp();

    default String getMessageId() {
        return null;
    }

    @Override
    default <T> DeviceMessage addHeader(HeaderKey<T> header, T value) {
        Message.super.addHeader(header, value);
        return this;
    }

    @Override
    DeviceMessage addHeader(String header, Object value);

    @Override
    default <T> DeviceMessage addHeaderIfAbsent(HeaderKey<T> header, T value) {
        Message.super.addHeaderIfAbsent(header, value);
        return this;
    }

    @Override
    DeviceMessage addHeaderIfAbsent(String header, Object value);

}
