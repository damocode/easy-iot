package org.damocode.iot.core.message.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.damocode.iot.core.message.CommonDeviceMessage;
import org.damocode.iot.core.message.MessageType;

/**
 * @Description: 上报设备属性,通常由设备定时上报,方向: 设备->平台
 * @Author: zzg
 * @Date: 2021/11/3 15:37
 * @Version: 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportPropertyMessage extends CommonDeviceMessage {

    private Object data;

    public MessageType getMessageType() {
        return MessageType.REPORT_PROPERTY;
    }

    public static ReportPropertyMessage create() {
        return new ReportPropertyMessage();
    }

    public ReportPropertyMessage setData(Object data) {
        this.data = data;
        return this;
    }

}
