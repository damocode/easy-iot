package org.damocode.iot.network.http.server.vertx;

import io.vertx.core.http.HttpServerOptions;
import lombok.*;

import java.util.Collections;
import java.util.Map;

/**
 * @Description:Http服务配置
 * @Author: zzg
 * @Date: 2021/10/26 11:20
 * @Version: 1.0.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpServerConfig {

    private String id;

    //服务实例数量(线程数)
    private int instance = Math.max(4, Runtime.getRuntime().availableProcessors());

    private int port;

    private String host = "0.0.0.0";

    private String certId;

    private boolean ssl;

    private HttpServerOptions options;

    private Map<String, String> httpHeaders;

    public Map<String, String> getHttpHeaders() {
        return nullMapHandle(httpHeaders);
    }

    private Map<String, String> nullMapHandle(Map<String, String> map) {
        return map == null ? Collections.emptyMap() : map;
    }

}
