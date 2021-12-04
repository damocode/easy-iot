package org.damocode.iot.core.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @Description: 缓存
 * @Author: zzg
 * @Date: 2021/10/12 9:22
 * @Version: 1.0.0
 */
public class Caches {

    private static final Supplier<ConcurrentMap<Object, Object>> cacheSupplier;

    static {
        cacheSupplier = Caches::createCaffeine;
    }

    private static ConcurrentMap<Object, Object> createCaffeine() {
        return Caffeine.newBuilder().build().asMap();
    }

    public static <K, V> ConcurrentMap<K, V> newCache() {
        return (ConcurrentMap) cacheSupplier.get();
    }

}
