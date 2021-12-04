package org.damocode.iot.core.utils;

/**
 * @Description: 系统工具类
 * @Author: zzg
 * @Date: 2021/10/26 15:23
 * @Version: 1.0.0
 */
public class SystemUtils {

    private static final float memoryWaterline = Math.max(
            0.1F,
            Float.parseFloat(System.getProperty("memory.waterline", "0.15"))
    );

    /**
     * 获取内存剩余比例,值为0-1之间,值越小,剩余可用内存越小
     *
     * @return 内存剩余比例
     */
    public static float getMemoryRemainder() {
        Runtime rt = Runtime.getRuntime();
        long free = rt.freeMemory();
        long total = rt.totalMemory();
        long max = rt.maxMemory();
        return (max - total + free) / (max + 0.0F);
    }

    private static volatile long outTimes = 0;

    /**
     * 判断当前内存是否已经超过水位线
     *
     * @return 是否已经超过水位线
     */
    public static boolean memoryIsOutOfWaterline() {
        boolean out = getMemoryRemainder() < memoryWaterline;
        if (!out) {
            outTimes = 0;
            return false;
        }
        if (outTimes == 0) {
            outTimes = System.currentTimeMillis();
        }else if (System.currentTimeMillis() - outTimes > 2000) {
            outTimes = 0;
            return true;
        }
        return false;
    }

}
