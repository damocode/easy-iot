package org.damocode.iot.core.topic;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description: 路由
 * @Author: zzg
 * @Date: 2021/10/26 15:49
 * @Version: 1.0.0
 */
public interface Router<T, R> {

    Router<T, R> route(String topic, Function<T, Supplier<R>> handler);

    Router<T, R> remove(String topic);

    List<Supplier<R>> execute(String topic, T data);

    void close();

    static <T, R> Router<T, R> create() {
        return new TopicRouter<>();
    }

}
