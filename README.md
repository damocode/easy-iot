# 介绍

 **easy-iotQQ开发交流群：51443279** 

easy-iot是一款轻量级物联网开发的sdk，开发人员通过如下三步就能够快速轻松实现设备的数据接入，设备控制（给设备发送指令）以及设备的状态管理（在线、离线等）等功能。
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

   实现DeviceMessageCodec接口

- 保存接收过来的数据
  
   实现DecodedClientMessageHandler接口

- 其他重要类或接口：

   DeviceOperatorManager 设备操作管理器 
  
   IDeviceOperatorService 设备状态操作接口，更新设备状态及获取设备状态

   DefaultDeviceSessionManager 设备会话管理器（easy-iot-spring-boot-starter）


# 最佳实现

### 架构图
![最佳实现架构图](https://images.gitee.com/uploads/images/2021/1010/182918_0d251104_1996367.jpeg "2.jpg")

### 推荐spring boot方式接入

- 导入依赖

```
<dependency>
   <groupId>org.damocode</groupId>
   <artifactId>easy-iot-spring-boot-starter</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```
[spring boot快速集成easy-iot物联网组件](https://gitee.com/damocode/easy-iot-spring-boot-starter)

[参考物联网快速接入demo](https://gitee.com/damocode/easy-iot-demo)


## 你的 `Star` 是我开发的动力