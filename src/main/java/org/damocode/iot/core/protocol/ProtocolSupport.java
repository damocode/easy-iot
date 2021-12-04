package org.damocode.iot.core.protocol;

import org.damocode.iot.core.device.*;
import org.damocode.iot.core.message.codec.DeviceMessageCodec;
import org.damocode.iot.core.message.codec.Transport;
import org.damocode.iot.core.server.ClientConnection;
import org.damocode.iot.core.server.DeviceGatewayContext;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @Description: 消息协议支持接口，通过实现此接口来自定义消息协议
 * @Author: zzg
 * @Date: 2021/10/14 11:14
 * @Version: 1.0.0
 */
public interface ProtocolSupport extends Ordered, Comparable<ProtocolSupport>{

    /**
     * 协议ID
     * @return
     */
    String getId();

    /**
     * 协议名称
     * @return
     */
    String getName();

    /**
     * 协议说明
     * @return
     */
    String getDescription();

    /**
     * @return 获取支持的协议类型
     */
    List<? extends Transport> getSupportedTransport();

    /**
     * 获取设备消息编码解码器
     * 1.用于将平台统一的消息对象转码为设备的消息
     * 2.用于将设备发送的消息转吗为平台统一的消息对象
     * @param transport
     * @return 消息编解码器
     */
    DeviceMessageCodec getMessageCodec(Transport transport);

    /**
     * 进行设备认证
     * @param request 认证请求，不同的连接方式实现不同
     * @return 认证结果
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * 获取自定义设备状态检查器,用于检查设备状态.
     * @return 设备状态检查器
     */
    default DeviceStateChecker getStateChecker() {
        return null;
    }

    default void onClientConnect(Transport transport, ClientConnection connection, DeviceGatewayContext context) {

    }

    @Override
    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    default int compareTo(ProtocolSupport o) {
        return Integer.compare(this.getOrder(), o == null ? 0 : o.getOrder());
    }

}
