package org.damocode.iot.core.message;

/**
 * @Description: 消息头
 * @Author: zzg
 * @Date: 2021/10/7 14:38
 * @Version: 1.0.0
 */
public interface Headers {


    /**
     * 保持在线,与{@link DeviceOnlineMessage}配合使用.
     */
    HeaderKey<Boolean> keepOnline = HeaderKey.of("keepOnline", true, Boolean.class);

    /**
     * 保持在线超时时间,超过指定时间未收到消息则认为离线
     */
    HeaderKey<Integer> keepOnlineTimeoutSeconds = HeaderKey.of("keepOnlineTimeoutSeconds", 600, Integer.class);

    /**
     * 发送既不管
     */
    HeaderKey<Boolean> sendAndForget = HeaderKey.of("sendAndForget", false);

    //分片数量
    HeaderKey<Integer> fragmentNumber = HeaderKey.of("frag_num", 0, Integer.class);

    //******** 分片消息,一个请求,设备将结果分片返回,通常用于处理大消息. **********
    //分片消息ID(为平台下发消息时的消息ID)
    HeaderKey<String> fragmentBodyMessageId = HeaderKey.of("frag_msg_id", null, String.class);

}
