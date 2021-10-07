package org.damocode.iot.core.message;

/**
 * @Description: 消息头
 * @Author: zzg
 * @Date: 2021/10/7 11:37
 * @Version: 1.0.0
 */
public interface HeaderKey<T> {

    String getKey();

    T getDefaultValue();

    default Class<T> getType(){
        return getDefaultValue() == null ? (Class<T>) Object.class : (Class<T>) getDefaultValue().getClass();
    }

    static <T> HeaderKey<T> of(String key, T defaultValue, Class<T> type) {
        return new HeaderKey<T>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public T getDefaultValue() {
                return defaultValue;
            }

            @Override
            public Class<T> getType() {
                return type;
            }
        };
    }

    @SuppressWarnings("all")
    static <T> HeaderKey<T> of(String key, T defaultValue) {
        return of(key, defaultValue, defaultValue == null ? (Class<T>) Object.class : (Class<T>) defaultValue.getClass());
    }

}
