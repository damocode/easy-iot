package org.damocode.iot.network.mqtt.client;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.damocode.iot.core.message.codec.MqttMessage;
import org.damocode.iot.core.message.codec.SimpleMqttMessage;
import org.damocode.iot.core.topic.Topic;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: Mqtt客户端实现类
 * @Author: zzg
 * @Date: 2021/10/12 9:07
 * @Version: 1.0.0
 */
@Slf4j
public class VertxMqttClient implements MqttClient {

    private final String id;

    @Getter
    private io.vertx.mqtt.MqttClient client;

    private final Topic<TopicSubject> subscriber = Topic.createRoot();

    private volatile boolean loading;

    private final List<Runnable> loadSuccessListener = new CopyOnWriteArrayList<>();

    public VertxMqttClient(String id) {
        this.id = id;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        if (!loading) {
            loadSuccessListener.forEach(Runnable::run);
            loadSuccessListener.clear();
        }
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void shutdown() {
        loading = false;
        if (isAlive()) {
            try {
                client.disconnect();
            } catch (Exception ignore) {
            }
            client = null;
        }
    }

    @Override
    public boolean isAlive() {
        return client != null && client.isConnected();
    }

    @Override
    public PublishSubject<MqttMessage> subscribe(List<String> topics, int qos, Action1<MqttMessage> action1) {
        PublishSubject<MqttMessage> subject = PublishSubject.create();
        List<Runnable> list = new ArrayList<>();
        for (String topic : topics) {
            String realTopic = parseTopic(topic);
            Topic<TopicSubject> subjectTopic = subscriber.append(realTopic.replace("#", "**").replace("+", "*"));
            TopicSubject topicQos = new TopicSubject(topic,subject,qos);
            boolean first = subjectTopic.getSubscribers().size() == 0;
            subjectTopic.subscribe(topicQos);
            list.add(() -> {
                if (subjectTopic.unsubscribe(topicQos).size() > 0) {
                    client.unsubscribe(convertMqttTopic(topic), result -> {
                        if (result.succeeded()) {
                            log.debug("unsubscribe mqtt topic {}", topic);
                        } else {
                            log.debug("unsubscribe mqtt topic {} error", topic, result.cause());
                        }
                    });
                }
            });

            //首次订阅
            if (isAlive() && first) {
                log.debug("subscribe mqtt topic {}", topic);
                client.subscribe(convertMqttTopic(topic), qos, result -> {
                    if (!result.succeeded()) {
                        subject.error(result.cause());
                    }
                });
            }
        }

        subject.subscribe(action1, throwable -> {

        },() -> list.forEach(runnable -> runnable.run()));

        return subject;
    }

    protected String parseTopic(String topic){
        //适配emqx共享订阅
        if (topic.startsWith("$share")) {
            return Stream.of(topic.split("/"))
                    .skip(2)
                    .collect(Collectors.joining("/", "/", ""));
        } else if (topic.startsWith("$queue")) {
            return topic.substring(6);
        }
        return topic;
    }

    @Override
    public void publish(MqttMessage message) {
        if (loading) {
            loadSuccessListener.add(() -> doPublish(message));
        }
        doPublish(message);
    }

    private void doPublish(MqttMessage message) {
        Buffer buffer = Buffer.buffer(message.getPayload());
        client.publish(message.getTopic(),
                buffer,
                MqttQoS.valueOf(message.getQosLevel()),
                message.isDup(),
                message.isRetain(),
                result -> {
                    if (result.succeeded()) {
                        log.info("publish mqtt [{}] message success: {}", client.clientId(), message);
                    } else {
                        log.info("publish mqtt [{}] message error : {}", client.clientId(), message, result.cause());
                    }
                });
    }

    public void setClient(io.vertx.mqtt.MqttClient client) {
        if (this.client != null && this.client != client) {
            try {
                this.client.disconnect();
            } catch (Exception ignore) {
            }
        }
        this.client = client;
        client.closeHandler(nil -> log.debug("mqtt client [{}] closed", id))
                .publishHandler(msg -> {
                    MqttMessage mqttMessage = SimpleMqttMessage
                            .builder()
                            .messageId(msg.messageId())
                            .topic(msg.topicName())
                            .payload(msg.payload().getByteBuf())
                            .dup(msg.isDup())
                            .retain(msg.isRetain())
                            .qosLevel(msg.qosLevel().value())
                            .build();
                    log.debug("handle mqtt message \n{}", mqttMessage);
                    String topic = msg.topicName().replace("#", "**").replace("+", "*");
                    ReplaySubject<Topic<VertxMqttClient.TopicSubject>> replaySubject = subscriber.findTopic(topic);
                    replaySubject.subscribe(topicMessageTopic -> {
                        Set<TopicSubject> sets = topicMessageTopic.getSubscribers();
                        sets.forEach(sub -> {
                            try{
                                PublishSubject<MqttMessage> subject = sub.getSubject();
                                subject.onNext(mqttMessage);
                            }catch (Exception e) {
                                log.error("handle mqtt message error", e);
                            }
                        });
                    });
                });
        if (loading) {
            loadSuccessListener.add(this::reSubscribe);
        } else if (isAlive()) {
            reSubscribe();
        }
    }

    private void reSubscribe() {
        subscriber.findTopic("/**").subscribe(topic -> {
            Set<TopicSubject> set = topic.getSubscribers();
            if(set.size() > 0){
                Map<String,Integer> map = set.stream().collect(Collectors.toMap(p -> convertMqttTopic(p.getTopic()), p -> p.getQos()));
                if(!map.isEmpty()){
                    log.debug("resubscribe mqtt topic {}", map);
                    client.subscribe(map);
                }
            }
        });

    }

    private String convertMqttTopic(String topic) {
        return topic.replace("**", "#").replace("*", "+");
    }


    @Getter
    @AllArgsConstructor
    class TopicSubject {
        private String topic;
        private PublishSubject<MqttMessage> subject;
        private Integer qos;
    }

}
