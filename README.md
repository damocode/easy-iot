# 介绍

easy-iot 是一款轻量级物联网开发的sdk，开发人员通过如下三步就能够轻松实现设备的数据接入，设备控制（给设备发送指令）以及设备的状态管理（在线、离线等）等功能。
- 创建网络组件及设备网关

例如下面代码创建一个tcp网络组件及设备网关

```java
TcpServerProperties properties = TcpServerProperties.builder()
                .id(IdUtil.fastUUID())
                .port(8888)
                .host("0.0.0.0")
                .options(new NetServerOptions())
                .parserSupplier(() -> new DirectRecordParser())
                .build();
        // 创建tcp网络组件
        TcpServer tcpServer = tcpServerProvider.createNetwork(properties);
        // 创建设备网关，并关联网络组件
        TcpServerDeviceGateway deviceGateway = new TcpServerDeviceGateway(tcpServer,
                deviceSessionManager,
                messageCodec,
                deviceOperatorManager,
                (deviceOperator, message) -> {
                    //保存设备数据
                    System.out.println(message);
                    return true;
                });
        deviceGateway.startup();
```
- 编写解析协议

- 保存接收过来的数据
