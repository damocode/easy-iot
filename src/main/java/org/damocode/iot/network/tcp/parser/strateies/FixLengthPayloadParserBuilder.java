package org.damocode.iot.network.tcp.parser.strateies;

import io.vertx.core.parsetools.RecordParser;
import org.damocode.iot.network.tcp.parser.PayloadParser;

/**
 * @Description: 固定长度粘拆包处理建造器
 * @Author: zzg
 * @Date: 2021/10/7 15:00
 * @Version: 1.0.0
 */
public class FixLengthPayloadParserBuilder extends VertxPayloadParserBuilder {

    public static PayloadParser build(int size) {
        return new RecordPayloadParser(() -> createParser(size));
    }

    protected static RecordParser createParser(int size) {
        return RecordParser.newFixed(size);
    }
}
