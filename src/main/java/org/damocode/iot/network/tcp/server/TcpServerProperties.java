package org.damocode.iot.network.tcp.server;

import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.SocketAddress;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.network.tcp.parser.PayloadParser;

import java.util.function.Supplier;

/**
 * @Description: Tcp服务属性
 * @Author: zzg
 * @Date: 2021/10/7 15:01
 * @Version: 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcpServerProperties {

    private String id;

    private NetServerOptions options;

    private Supplier<PayloadParser> parserSupplier;

    private String host;

    private int port;

    private boolean ssl;

    //服务实例数量(线程数)
    private int instance = Runtime.getRuntime().availableProcessors();

    private long keepAliveTimeout;

    public SocketAddress createSocketAddress() {
        if (StringUtils.isEmpty(host)) {
            host = "localhost";
        }
        return SocketAddress.inetSocketAddress(port, host);
    }

}

