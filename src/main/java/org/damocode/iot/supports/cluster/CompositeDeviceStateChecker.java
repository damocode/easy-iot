package org.damocode.iot.supports.cluster;

import org.damocode.iot.core.device.DeviceOperator;
import org.damocode.iot.core.device.DeviceStateChecker;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: 设备状态组合检查器
 * @Author: zzg
 * @Date: 2021/10/7 11:56
 * @Version: 1.0.0
 */
public class CompositeDeviceStateChecker implements DeviceStateChecker {

    private final List<DeviceStateChecker> checkerList = new CopyOnWriteArrayList<>();

    public void addDeviceStateChecker(DeviceStateChecker checker) {
        checkerList.add(checker);
        checkerList.sort(Comparator.comparing(DeviceStateChecker::order));
    }

    @Override
    public Byte checkState(DeviceOperator device) {
        if(checkerList.isEmpty()){
            return null;
        }
        if (checkerList.size() == 1) {
            return checkerList.get(0).checkState(device);
        }
        Byte checker = checkerList.get(0).checkState(device);
        for (int i = 1, len = checkerList.size(); i < len; i++) {
            if(checker == null){
                checker = checkerList.get(i).checkState(device);
            }
        }
        return checker;
    }
}

