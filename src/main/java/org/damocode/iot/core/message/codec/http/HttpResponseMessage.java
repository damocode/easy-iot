package org.damocode.iot.core.message.codec.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * @Description: http响应消息
 * @Author: zzg
 * @Date: 2021/10/26 10:40
 * @Version: 1.0.0
 */
public interface HttpResponseMessage extends HttpMessage {

    int getStatus();

    MediaType getContentType();

    default String print() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP").append(" ").append(HttpStatus.resolve(getStatus())).append("\n");
        boolean hasContentType = false;
        for (Header header : getHeaders()) {
            if (HttpHeaders.CONTENT_TYPE.equals(header.getName())) {
                hasContentType = true;
            }
            builder
                    .append(header.getName()).append(": ").append(String.join(",", header.getValue()))
                    .append("\n");
        }
        if (!hasContentType && null != getContentType()) {
            builder.append("Content-Type: ").append(getContentType()).append("\n");
        }
        return print(builder);
    }

}
