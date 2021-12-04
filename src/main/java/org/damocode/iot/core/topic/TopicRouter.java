package org.damocode.iot.core.topic;

import lombok.extern.slf4j.Slf4j;
import rx.subjects.ReplaySubject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Description: 主题路由
 * @Author: zzg
 * @Date: 2021/10/26 15:49
 * @Version: 1.0.0
 */
@Slf4j
public class TopicRouter<T, R> implements Router<T, R> {

    private final Topic<Function<T, Supplier<R>>> root = Topic.createRoot();

    @Override
    public Router<T, R> route(String topic, Function<T, Supplier<R>> handler) {
        root.append(topic).subscribe(handler);
        return this;
    }

    @Override
    public Router<T, R> remove(String topic) {
        root.getTopic(topic).ifPresent(Topic::unsubscribeAll);
        return this;
    }

    @Override
    public List<Supplier<R>> execute(String topic, T data) {
        AtomicReference<List<Supplier<R>>> result = new AtomicReference<>();
        ReplaySubject<Topic<Function<T,Supplier<R>>>> subject = root.findTopic(topic);
        subject.subscribe(functionTopic -> {
            Set<Function<T,Supplier<R>>> sets = functionTopic.getSubscribers();
            if(sets == null || sets.isEmpty()){
                log.debug("not handler for {}", topic);
                return;
            };
            List<Supplier<R>> list = sets.stream().distinct().map(runner -> runner.apply(data)).collect(Collectors.toList());
            result.set(list);
        });
        return result.get();
    }

    @Override
    public void close() {
        root.clean();
    }

}

