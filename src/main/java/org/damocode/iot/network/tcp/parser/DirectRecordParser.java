package org.damocode.iot.network.tcp.parser;

import io.vertx.core.buffer.Buffer;
import rx.subjects.PublishSubject;

/**
 * @Description: 不进行任何粘拆包处理
 * @Author: zzg
 * @Date: 2021/10/7 15:00
 * @Version: 1.0.0
 */
public class DirectRecordParser implements PayloadParser {

    PublishSubject<Buffer> processor = PublishSubject.create();

    @Override
    public void handle(Buffer buffer) {
        processor.onNext(buffer);
    }

    @Override
    public PublishSubject<Buffer> handlePayload() {
        return processor;
    }

    @Override
    public void close() {
        processor.onCompleted();
    }
}
