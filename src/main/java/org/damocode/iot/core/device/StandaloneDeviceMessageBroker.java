package org.damocode.iot.core.device;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.message.DeviceMessageReply;
import org.damocode.iot.core.message.Headers;
import org.damocode.iot.core.message.Message;
import org.damocode.iot.core.server.MessageHandler;
import rx.subjects.PublishSubject;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 单机版设备操作代理
 * @Author: zzg
 * @Date: 2021/10/7 14:36
 * @Version: 1.0.0
 */
@Slf4j
public class StandaloneDeviceMessageBroker implements DeviceOperationBroker, MessageHandler {

    private final PublishSubject<Message> subject = PublishSubject.create();

    private final Map<String,PublishSubject<DeviceMessageReply>> replyProcessor = new ConcurrentHashMap<>();

    private final Map<String, AtomicInteger> partCache = new ConcurrentHashMap<>();

    private final Map<String, Function<String,DeviceStateInfo>> stateHandler = new ConcurrentHashMap<>();

    public PublishSubject<Message> handleSendToDeviceMessage(String serverId) {
        return subject;
    }

    @Override
    public void handleGetDeviceState(String serverId, Function<String, DeviceStateInfo> stateMapper) {
        stateHandler.put(serverId, stateMapper);
        stateHandler.remove(serverId);
    }

    public Boolean reply(DeviceMessageReply message) {
        String messageId = message.getMessageId();
        if (StringUtils.isEmpty(messageId)) {
            log.warn("reply message messageId is empty: {}", message);
            return false;
        }
        String partMsgId = message.getHeader(Headers.fragmentBodyMessageId).orElse(null);
        if (partMsgId != null) {
            PublishSubject<DeviceMessageReply> processor = replyProcessor.getOrDefault(partMsgId, replyProcessor.get(messageId));
            if(processor == null){
                replyProcessor.remove(partMsgId);
                return false;
            }
            int partTotal = message.getHeader(Headers.fragmentNumber).orElse(1);
            AtomicInteger counter = partCache.computeIfAbsent(partMsgId, ignore -> new AtomicInteger(partTotal));
            processor.onNext(message);
            if (counter.decrementAndGet() <= 0) {
                processor.onCompleted();
                replyProcessor.remove(partMsgId);
            }
            return true;
        }
        PublishSubject<DeviceMessageReply> processor = replyProcessor.get(messageId);
        if(processor != null){
            processor.onNext(message);
            processor.onCompleted();
        } else {
            replyProcessor.remove(messageId);
            return false;
        }
        return true;
    }

    public PublishSubject<DeviceMessageReply> handleReply(String deviceId, String messageId, Duration timeout) {
        PublishSubject<DeviceMessageReply> process = replyProcessor.computeIfAbsent(messageId, ignore -> PublishSubject.create());
        process.timeout(timeout.getSeconds(), TimeUnit.SECONDS);
        return replyProcessor.remove(messageId);
    }

    public void send(String serverId,Message message) {
        if(!subject.hasObservers()){
            return;
        }
        subject.onNext(message);
    }

    @Override
    public List<DeviceStateInfo> getDeviceState(String serviceId, Collection<String> deviceIdList) {
        Function<String,DeviceStateInfo> fun = stateHandler.get(serviceId);
        return deviceIdList.stream().map(id -> fun.apply(id))
                .collect(Collectors.toList());
    }

}
