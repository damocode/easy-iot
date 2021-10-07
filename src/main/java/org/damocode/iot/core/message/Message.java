package org.damocode.iot.core.message;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;

import java.util.Map;
import java.util.Optional;

import static org.damocode.iot.core.message.MessageType.UNKNOWN;

/**
 * @Description: 消息
 * @Author: zzg
 * @Date: 2021/10/7 11:33
 * @Version: 1.0.0
 */
public interface Message {

    default MessageType getMessageType() {
        return UNKNOWN;
    }

    /**
     * 消息的唯一标识,用于在请求响应模式下对请求和响应进行关联.
     * @return
     */
    String getMessageId();

    long getTimestamp();

    /**
     * 消息头,用于自定义一些消息行为
     * @return
     */
    Map<String, Object> getHeaders();

    /**
     * 添加一个header
     * @param header
     * @param value
     * @return
     */
    Message addHeader(String header, Object value);

    /**
     * 添加header,如果header已存在则放弃
     * @param header
     * @param value
     * @return
     */
    Message addHeaderIfAbsent(String header, Object value);

    /**
     * 删除一个header
     * @param header
     * @return
     */
    Message removeHeader(String header);

    default <T> Message addHeader(HeaderKey<T> header, T value) {
        return addHeader(header.getKey(), value);
    }

    default <T> Message addHeaderIfAbsent(HeaderKey<T> header, T value) {
        return addHeaderIfAbsent(header.getKey(), value);
    }

    default <T> Optional<T> getHeader(HeaderKey<T> key) {
        return getHeader(key.getKey())
                .map(v -> TypeUtils.cast(v, key.getType(), ParserConfig.global));
    }

    default <T> T getHeaderOrDefault(HeaderKey<T> key) {
        return getHeader(key).orElseGet(key::getDefaultValue);
    }

    default Optional<Object> getHeader(String header) {
        return Optional.ofNullable(getHeaders())
                .map(headers -> headers.get(header));
    }

    default void validate(){

    }

}
