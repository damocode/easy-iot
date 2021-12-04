package org.damocode.iot.core.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.beans.ConstructorProperties;

/**
 * @Description: 常规Mqtt消息
 * @Author: zzg
 * @Date: 2021/10/12 9:25
 * @Version: 1.0.0
 */
public class SimpleMqttMessage implements MqttMessage {
    private String topic;
    private String clientId;
    private int qosLevel;
    private ByteBuf payload;
    private int messageId;
    private boolean will;
    private boolean dup;
    private boolean retain;
    private MessagePayloadType payloadType;

    public static SimpleMqttMessage.SimpleMqttMessageBuilder builder() {
        return new SimpleMqttMessage.SimpleMqttMessageBuilder();
    }

    public String toString() {
        return this.print();
    }

    public static SimpleMqttMessage of(String str) {
        SimpleMqttMessage mqttMessage = new SimpleMqttMessage();
        TextMessageParser.of((start) -> {
            String[] qosAndTopic = start.split("[ ]");
            if (qosAndTopic.length == 1) {
                mqttMessage.setTopic(qosAndTopic[0]);
            } else {
                mqttMessage.setTopic(qosAndTopic[1]);
                String qos = qosAndTopic[0].toLowerCase();
                if (qos.length() == 1) {
                    mqttMessage.setQosLevel(Integer.parseInt(qos));
                } else {
                    mqttMessage.setQosLevel(Integer.parseInt(qos.substring(qos.length() - 1)));
                }
            }

        }, (header, value) -> {
        }, (body) -> {
            mqttMessage.setPayload(Unpooled.wrappedBuffer(body.getBody()));
            mqttMessage.setPayloadType(body.getType());
        }, () -> {
            mqttMessage.setPayload(Unpooled.wrappedBuffer(new byte[0]));
        }).parse(str);
        return mqttMessage;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getClientId() {
        return this.clientId;
    }

    public int getQosLevel() {
        return this.qosLevel;
    }

    public ByteBuf getPayload() {
        return this.payload;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public boolean isWill() {
        return this.will;
    }

    public boolean isDup() {
        return this.dup;
    }

    public boolean isRetain() {
        return this.retain;
    }

    public MessagePayloadType getPayloadType() {
        return this.payloadType;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setQosLevel(int qosLevel) {
        this.qosLevel = qosLevel;
    }

    public void setPayload(ByteBuf payload) {
        this.payload = payload;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setWill(boolean will) {
        this.will = will;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public void setPayloadType(MessagePayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public SimpleMqttMessage() {
    }

    @ConstructorProperties({"topic", "clientId", "qosLevel", "payload", "messageId", "will", "dup", "retain", "payloadType"})
    public SimpleMqttMessage(String topic, String clientId, int qosLevel, ByteBuf payload, int messageId, boolean will, boolean dup, boolean retain, MessagePayloadType payloadType) {
        this.topic = topic;
        this.clientId = clientId;
        this.qosLevel = qosLevel;
        this.payload = payload;
        this.messageId = messageId;
        this.will = will;
        this.dup = dup;
        this.retain = retain;
        this.payloadType = payloadType;
    }

    public static class SimpleMqttMessageBuilder {
        private String topic;
        private String clientId;
        private int qosLevel;
        private ByteBuf payload;
        private int messageId;
        private boolean will;
        private boolean dup;
        private boolean retain;
        private MessagePayloadType payloadType;

        SimpleMqttMessageBuilder() {
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder body(String payload) {
            return this.payload(payload.getBytes());
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder body(byte[] payload) {
            return this.payload(Unpooled.wrappedBuffer(payload));
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder payload(String payload) {
            return this.payload(payload.getBytes());
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder payload(byte[] payload) {
            return this.payload(Unpooled.wrappedBuffer(payload));
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder qosLevel(int qosLevel) {
            this.qosLevel = qosLevel;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder payload(ByteBuf payload) {
            this.payload = payload;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder will(boolean will) {
            this.will = will;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder dup(boolean dup) {
            this.dup = dup;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder retain(boolean retain) {
            this.retain = retain;
            return this;
        }

        public SimpleMqttMessage.SimpleMqttMessageBuilder payloadType(MessagePayloadType payloadType) {
            this.payloadType = payloadType;
            return this;
        }

        public SimpleMqttMessage build() {
            return new SimpleMqttMessage(this.topic, this.clientId, this.qosLevel, this.payload, this.messageId, this.will, this.dup, this.retain, this.payloadType);
        }

        public String toString() {
            return "SimpleMqttMessage.SimpleMqttMessageBuilder(topic=" + this.topic + ", clientId=" + this.clientId + ", qosLevel=" + this.qosLevel + ", payload=" + this.payload + ", messageId=" + this.messageId + ", will=" + this.will + ", dup=" + this.dup + ", retain=" + this.retain + ", payloadType=" + this.payloadType + ")";
        }
    }
}
