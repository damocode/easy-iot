package org.damocode.iot.network.tcp.parser;

import io.vertx.core.buffer.Buffer;
import rx.subjects.PublishSubject;

/**
 * @Description: 用于处理TCP粘拆包的解析器,通常一个客户端对应一个解析器.
 * @Author: zzg
 * @Date: 2021/10/7 14:59
 * @Version: 1.0.0
 */
public interface PayloadParser {

    /**
     * 处理一个数据包
     * @param buffer 数据包
     */
    void handle(Buffer buffer);

    /**
     * 订阅完整的数据包流,每一个元素为一个完整的数据包
     * @return 完整数据包流
     */
    PublishSubject<Buffer> handlePayload();

    /**
     * 关闭以释放相关资源
     */
    void close();

    /**
     * 重置规则
     */
    default void reset(){}

}

