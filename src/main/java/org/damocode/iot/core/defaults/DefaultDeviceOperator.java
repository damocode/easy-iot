package org.damocode.iot.core.defaults;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.damocode.iot.core.device.*;
import org.damocode.iot.core.message.DisconnectDeviceMessage;
import org.damocode.iot.core.protocol.ProtocolSupport;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

/**
 * @Description: 默认设备操作器
 * @Author: zzg
 * @Date: 2021/10/7 11:49
 * @Version: 1.0.0
 */
@Slf4j
public class DefaultDeviceOperator implements DeviceOperator {

    public static final DeviceStateChecker DEFAULT_STATE_CHECKER = device -> checkState0(((DefaultDeviceOperator) device));

    private final String id;

    private final DeviceOperationBroker handler;

    private final DeviceMessageSender messageSender;

    private final DeviceStateChecker stateChecker;

    private final IDeviceOperatorService deviceOperatorService;

    private final DeviceOperatorInfo operatorInfo;

    public DefaultDeviceOperator(String id,
                                 DeviceOperationBroker handler,
                                 IDeviceOperatorService deviceOperatorService) {
        this(id,handler,deviceOperatorService,DEFAULT_STATE_CHECKER);
    }

    public DefaultDeviceOperator(String id,
                                 DeviceOperationBroker handler,
                                 IDeviceOperatorService deviceOperatorService,
                                 DeviceStateChecker deviceStateChecker) {
        this.id = id;
        this.handler = handler;
        this.messageSender = new DefaultDeviceMessageSender(handler,this);
        this.deviceOperatorService = deviceOperatorService;
        this.operatorInfo = deviceOperatorService.getByDeviceId(id);
        this.stateChecker = deviceStateChecker;
    }

    @Override
    public String getDeviceId() {
        return id;
    }

    @Override
    public String getConnectionServerId() {
        if(operatorInfo == null){
            return null;
        }
        return operatorInfo.getServerId();
    }

    @Override
    public String getSessionId() {
        if(operatorInfo == null){
            return null;
        }
        return operatorInfo.getSessionId();
    }

    @Override
    public String getAddress() {
        if(operatorInfo == null){
            return null;
        }
        return operatorInfo.getAddress();
    }

    @Override
    public Boolean putState(byte state) {
        if(operatorInfo == null){
            return false;
        }
        operatorInfo.setState(state);
        return deviceOperatorService.updateByDeviceId(operatorInfo);
    }

    @Override
    public Byte getState() {
        if(operatorInfo == null){
            return DeviceState.unknown;
        }
        return Optional.ofNullable(operatorInfo.getState())
                .orElse(DeviceState.unknown);
    }

    @Override
    public Byte checkState() {
        if(operatorInfo == null){
            return DeviceState.unknown;
        }
        Byte newer = stateChecker.checkState(this);
        if(newer == null){
            newer = DEFAULT_STATE_CHECKER.checkState(this);
        }
        if(newer == null){
            newer = DeviceState.online;
        }
        Byte old = this.getState();
        //状态不一致?
        if (newer != old) {
            log.info("device[{}] state changed from {} to {}", this.getDeviceId(), old, newer);
            this.operatorInfo.setState(newer);
            if (newer == DeviceState.online) {
                this.operatorInfo.setOnlineTime(new Date());
            }else if (newer == DeviceState.offline){
                this.operatorInfo.setOfflineTime(new Date());
            }
            deviceOperatorService.updateByDeviceId(operatorInfo);
        }
        return newer;
    }

    @Override
    public Boolean online(String serverId, String sessionId, String address) {
        if(operatorInfo == null){
            return false;
        }
        operatorInfo.setServerId(serverId);
        operatorInfo.setSessionId(sessionId);
        operatorInfo.setAddress(address);
        operatorInfo.setState(DeviceState.online);
        operatorInfo.setOnlineTime(new Date());
        return deviceOperatorService.updateByDeviceId(operatorInfo);
    }

    @Override
    public Boolean offline() {
        if(operatorInfo == null){
            return false;
        }
        operatorInfo.setServerId("");
        operatorInfo.setSessionId("");
        operatorInfo.setState(DeviceState.offline);
        operatorInfo.setOfflineTime(new Date());
        return deviceOperatorService.updateByDeviceId(operatorInfo);
    }

    @Override
    public Boolean disconnect() {
        DisconnectDeviceMessage disconnect = new DisconnectDeviceMessage();
        disconnect.setDeviceId(getDeviceId());
        disconnect.setMessageId(IdUtil.fastUUID());
        return messageSender().send(disconnect);
    }

    @Override
    public DeviceMessageSender messageSender() {
        return this.messageSender;
    }

    private Byte doCheckState() {
        String serverId = this.getConnectionServerId();
        Byte state = this.getState();
        //如果缓存中存储有当前设备所在服务信息则尝试发起状态检查
        if (StringUtils.isNotBlank(serverId)) {
            return handler.getDeviceState(serverId, Collections.singletonList(id))
                    .stream()
                    .map(DeviceStateInfo::getState)
                    .findFirst()
                    .orElse(state);
        }
        //如果是在线状态,则改为离线,否则保持状态不变
        if (state.equals(DeviceState.online)) {
            return DeviceState.offline;
        }
        return state;
    }

    private static Byte checkState0(DefaultDeviceOperator operator) {
        return operator.doCheckState();
    }

}

