package org.damocode.iot.core.message.codec.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: http 请求头
 * @Author: zzg
 * @Date: 2021/10/26 10:18
 * @Version: 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    private String name;

    private String[] value;

    private String firstValue() {
        return (value != null && value.length > 0) ? value[0] : null;
    }

}
