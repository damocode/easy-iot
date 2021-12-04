package org.damocode.iot.network.tcp.parser.strateies;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import org.damocode.iot.network.tcp.parser.PayloadParser;
import rx.subjects.PublishSubject;

import java.util.function.Supplier;

/**
 * @Description: 粘拆包处理建造器抽象类
 * @Author: zzg
 * @Date: 2021/10/7 15:00
 * @Version: 1.0.0
 */
public abstract class VertxPayloadParserBuilder {

    static class RecordPayloadParser implements PayloadParser {
        private final Supplier<RecordParser> recordParserSupplier;
        private final PublishSubject<Buffer> processor = PublishSubject.create();

        private RecordParser recordParser;

        public RecordPayloadParser(Supplier<RecordParser> recordParserSupplier) {
            this.recordParserSupplier = recordParserSupplier;
            reset();
        }

        @Override
        public void handle(Buffer buffer) {
            recordParser.handle(buffer);
        }

        @Override
        public PublishSubject<Buffer> handlePayload() {
            return processor;
        }

        @Override
        public void close() {
            processor.onCompleted();
        }

        @Override
        public void reset() {
            this.recordParser = recordParserSupplier.get();
            this.recordParser.handler(processor::onNext);
        }
    }

}
