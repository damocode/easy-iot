package org.damocode.iot.core.message;

import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zzg
 * @Date: 2021/10/7 11:42
 * @Version: 1.0.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonDeviceMessageReply<ME extends CommonDeviceMessageReply> implements DeviceMessageReply {

    private boolean success = true;

    private String code;

    private String message;

    private String messageId;

    private String deviceId;

    private long timestamp = System.currentTimeMillis();

    private Map<String, Object> headers;

    @Override
    public synchronized ME addHeaderIfAbsent(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.putIfAbsent(header, value);
        }
        return (ME) this;
    }

    @Override
    public synchronized ME addHeader(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.put(header, value);
        }
        return (ME) this;
    }

    @Override
    public ME removeHeader(String header) {
        if (headers != null) {
            this.headers.remove(header);
        }
        return (ME) this;
    }

    public ME code(String code) {
        this.code = code;
        return (ME) this;
    }

    public ME message(String message) {
        this.message = message;
        return (ME) this;
    }

    public ME deviceId(String deviceId) {
        this.deviceId = deviceId;
        return (ME) this;
    }

    @Override
    public ME success() {
        success = true;
        return (ME) this;
    }

    public ME error(Throwable e) {
        success = false;
        setMessage(e.getMessage());
        addHeader("errorType", e.getClass().getName());
        addHeader("errorMessage", e.getMessage());
        return ((ME) this);
    }

    @Override
    public ME from(Message message) {
        this.messageId = message.getMessageId();
        if (message instanceof DeviceMessage) {
            this.deviceId = ((DeviceMessage) message).getDeviceId();
        }
        return (ME) this;
    }

    @Override
    public ME messageId(String messageId) {
        this.messageId = messageId;
        return (ME) this;
    }

    @Override
    public <T> ME addHeader(HeaderKey<T> header, T value) {
        return (ME) DeviceMessageReply.super.addHeader(header, value);
    }

}

