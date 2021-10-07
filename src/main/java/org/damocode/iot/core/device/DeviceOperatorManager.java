package org.damocode.iot.core.device;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.defaults.DefaultDeviceOperator;
import org.damocode.iot.supports.cluster.CompositeDeviceStateChecker;

import java.time.Duration;

/**
 * @Description: 设备操作管理器
 * @Author: zzg
 * @Date: 2021/10/7 11:57
 * @Version: 1.0.0
 */
public class DeviceOperatorManager {

    //设备操作
    private final DeviceOperationBroker handler;

    private final IDeviceOperatorService deviceOperatorService;

    //缓存
    private final Cache<String, DeviceOperator> operatorCache;

    //状态检查器
    private final CompositeDeviceStateChecker stateChecker = new CompositeDeviceStateChecker();

    public DeviceOperatorManager(DeviceOperationBroker handler,IDeviceOperatorService deviceOperatorService) {
        this(handler, deviceOperatorService, CacheBuilder
                .newBuilder()
                .softValues()
                .expireAfterAccess(Duration.ofMinutes(30))
                .build());
    }

    public DeviceOperatorManager(DeviceOperationBroker handler,IDeviceOperatorService deviceOperatorService, Cache<String, DeviceOperator> cache){
        this.handler = handler;
        this.deviceOperatorService = deviceOperatorService;
        this.operatorCache = cache;
        this.addStateChecker(DefaultDeviceOperator.DEFAULT_STATE_CHECKER);
    }

    public DeviceOperator getDevice(String id) {
        if(StringUtils.isBlank(id)){
            return null;
        }
        {
            DeviceOperator deviceOperator = operatorCache.getIfPresent(id);
            if(deviceOperator != null){
                return deviceOperator;
            }
        }
        DeviceOperator deviceOperator = createOperator(id);
        operatorCache.put(id,deviceOperator);
        return deviceOperator;
    }

    public void addStateChecker(DeviceStateChecker deviceStateChecker) {
        this.stateChecker.addDeviceStateChecker(deviceStateChecker);
    }

    private DefaultDeviceOperator createOperator(String deviceId) {
        return new DefaultDeviceOperator(deviceId,  handler, deviceOperatorService, stateChecker);
    }

}

