package org.damocode.iot.core.message.codec.http;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: http 请求消息
 * @Author: zzg
 * @Date: 2021/10/26 10:16
 * @Version: 1.0.0
 */
public interface HttpRequestMessage extends HttpMessage {

    default String getPath() {
        return HttpUtils.getUrlPath(getUrl());
    }

    String getUrl();

    HttpMethod getMethod();

    MediaType getContentType();

    Map<String, String> getQueryParameters();

    default Map<String, String> getRequestParam() {
        if (MediaType.APPLICATION_FORM_URLENCODED.includes(getContentType())) {
            return HttpUtils.parseEncodedUrlParams(payloadAsString());
        }
        return Collections.emptyMap();
    }

    default Object parseBody() {
        if (MediaType.APPLICATION_JSON.includes(getContentType())) {
            return JSON.parse(payloadAsBytes());
        }

        if (MediaType.APPLICATION_FORM_URLENCODED.includes(getContentType())) {
            return HttpUtils.parseEncodedUrlParams(payloadAsString());
        }

        return payloadAsString();
    }

    default Optional<String> getQueryParameter(String name) {
        return Optional.ofNullable(getQueryParameters())
                .map(map -> map.get(name));
    }

    default String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(getMethod()).append(" ").append(getPath());
        if (!CollectionUtils.isEmpty(getQueryParameters())) {
            builder.append("?").append(getQueryParameters().entrySet().stream()
                    .map(e -> e.getKey().concat("=").concat(e.getValue()))
                    .collect(Collectors.joining("&")))
                    .append("\n");
        } else {
            builder.append("\n");
        }
        for (Header header : getHeaders()) {
            builder
                    .append(header.getName()).append(": ").append(String.join(",", header.getValue()))
                    .append("\n");
        }
        return print(builder);
    }

}
