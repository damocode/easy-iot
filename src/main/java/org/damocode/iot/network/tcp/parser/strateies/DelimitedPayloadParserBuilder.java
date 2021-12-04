package org.damocode.iot.network.tcp.parser.strateies;

import io.vertx.core.parsetools.RecordParser;
import org.apache.commons.lang.StringEscapeUtils;
import org.damocode.iot.network.tcp.parser.PayloadParser;

/**
 * @Description: 分割符粘拆包处理建造器
 * @Author: zzg
 * @Date: 2021/10/7 15:00
 * @Version: 1.0.0
 */
public class DelimitedPayloadParserBuilder extends VertxPayloadParserBuilder {

    public static PayloadParser build(String delim) {
        return new RecordPayloadParser(() -> createParser(delim));
    }

    protected static RecordParser createParser(String delim) {
        return RecordParser.newDelimited(StringEscapeUtils.unescapeJava(delim));
    }

}
