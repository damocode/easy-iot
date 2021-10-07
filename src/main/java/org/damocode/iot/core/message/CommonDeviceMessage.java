package org.damocode.iot.core.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zzg
 * @Date: 2021/10/7 11:38
 * @Version: 1.0.0
 */
@Getter
@Setter
public class CommonDeviceMessage implements DeviceMessage {

    private String messageId;

    private String deviceId;

    private long timestamp = System.currentTimeMillis();

    private Map<String, Object> headers;

    @Override
    public synchronized DeviceMessage addHeader(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.put(header, value);
        }
        return this;
    }

    @Override
    public synchronized DeviceMessage addHeaderIfAbsent(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.putIfAbsent(header, value);
        }
        return this;
    }

    @Override
    public DeviceMessage removeHeader(String header) {
        if (this.headers != null) {
            this.headers.remove(header);
        }
        return this;
    }

}
