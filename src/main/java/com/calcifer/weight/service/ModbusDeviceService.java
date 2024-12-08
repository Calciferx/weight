package com.calcifer.weight.service;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.dto.ModBusDeviceSerialSort;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 串口和ModBus设备控制
 */
@Service
@Slf4j
public class ModbusDeviceService {
    private ModbusMaster modbusMaster;

    @Value("${calcifer.weight.slave-ip}")
    private String slaveIp;

    @Value("${calcifer.weight.coil-num}")
    private Integer coilNum;

    @Value("${calcifer.weight.enable-modbus-device-init: true}")
    private boolean enableModbusDeviceInit;

    @Value("${calcifer.weight.modbus-control-sleep-time: 100}")
    private int modbusControlSleepTime;


    @Getter
    private ModBusDeviceStatus lastModBusDeviceStatus;

    @Getter
    private boolean init;

    @Value("${calcifer.weight.modbus-device-info-path}")
    private String modbusDeviceInfoPath;


    public void init() {
        log.info("init devices...");
        if (enableModbusDeviceInit) {
            initModbusDevice();
        } else {
            log.info("enableDeviceInit is false, init end.");
        }
        init = true;
    }

    private void initModbusDevice() {
        String modbusDeviceInfoJson = FileUtil.readUtf8String(modbusDeviceInfoPath);
        List<ModBusDeviceSerialSort> deviceSerialSortList = JSON.parseArray(modbusDeviceInfoJson, ModBusDeviceSerialSort.class);
        if (!deviceSerialSortList.isEmpty()) WeightContext.front = deviceSerialSortList.get(0);
        if (deviceSerialSortList.size() > 1) WeightContext.back = deviceSerialSortList.get(1);

        initModbusMaster();
        // 关闭道闸 ON先置为false解控，OFF置为true受控，再OFF置为false解除控制
        log.info("closing barriers...");
        controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
        controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
        controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
        controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
        controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
        controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);

        controlModBusDevice(WeightContext.back.getBarrierGateOn(), false);
        controlModBusDevice(WeightContext.back.getBarrierGateOn(), false);
        controlModBusDevice(WeightContext.back.getBarrierGateOff(), true);
        controlModBusDevice(WeightContext.back.getBarrierGateOff(), true);
        controlModBusDevice(WeightContext.back.getBarrierGateOff(), false);
        controlModBusDevice(WeightContext.back.getBarrierGateOff(), false);

        // 红绿灯置为绿
        log.info("set all light green...");
        controlModBusDevice(WeightContext.front.getTrafficLight(), false);
        controlModBusDevice(WeightContext.front.getTrafficLight(), false);
        controlModBusDevice(WeightContext.back.getTrafficLight(), false);
        controlModBusDevice(WeightContext.back.getTrafficLight(), false);
    }

    @PreDestroy
    public void destroy() throws ModbusIOException {
        log.info("destroy...disconnect modbus devices");
        // modbus
        if (modbusMaster != null) {
            log.info("modbusMaster is not null, disconnecting modbus devices");
            modbusMaster.disconnect();
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 10, backoff = @Backoff(delay = 100, multiplier = 2))
    public void controlModBusDevice(int serialPort, boolean status) {
        log.info("control modbus device: {}, status: {}", serialPort, status);
        try {
            modbusMaster.writeSingleCoil(1, serialPort, status);
            Thread.sleep(modbusControlSleepTime);
        } catch (ModbusProtocolException e) {
            log.info("ModbusProtocolException: {}", e.getMessage());
            throw new RuntimeException("ModbusProtocolException: " + e.getMessage());
        } catch (ModbusNumberException e) {
            log.info("ModbusNumberException: {}", e.getMessage());
            throw new RuntimeException("ModbusNumberException: " + e.getMessage());
        } catch (ModbusIOException e) {
            log.info("ModbusIOException: {}", e.getMessage());
            throw new RuntimeException("ModbusIOException: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void recover(Exception e) {
        log.error("RETRY FAILED！");
    }

    /**
     * 创建ModBus连接
     */
    private void initModbusMaster() {
        log.info("create modbus connection...");
        try {
            InetAddress ip = InetAddress.getByName(slaveIp);
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(ip);
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(Modbus.TCP_PORT);
            modbusMaster = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        } catch (UnknownHostException e) {
            log.info("Create Modbus connection to {} error. ", slaveIp, e);
        }
    }

    /**
     * 读取ModBus设备状态（红外、红绿灯、道闸）
     */
    public ModBusDeviceStatus readModBusDeviceStatus() throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
        int slaveAddress = 1;
        int offset = 0;
        int quantity = coilNum;
        boolean[] discreteInputs = modbusMaster.readDiscreteInputs(slaveAddress, offset, quantity);
        ModBusDeviceStatus modBusDeviceStatus = new ModBusDeviceStatus(discreteInputs);
        String statusChangeStr = modBusDeviceStatus.getStatusChangeStr(lastModBusDeviceStatus);
        if (StringUtils.hasText(statusChangeStr)) {
            log.info(statusChangeStr);
        }
        lastModBusDeviceStatus = modBusDeviceStatus;
        return modBusDeviceStatus;
    }

    public class ModBusDeviceStatus {
        private final boolean[] discreteInputs;

        private ModBusDeviceStatus(boolean[] discreteInputs) {
            this.discreteInputs = discreteInputs;
        }

        @Override
        public String toString() {
            return String.format("device status: %s, front infra1:%s, front infra2:%s, back infra2:%s, back infra1:%s", JSON.toJSONString(discreteInputs), isFrontInfrared1(), isFrontInfrared2(), isBackInfrared2(), isBackInfrared1());
        }

        public String getStatusChangeStr(ModBusDeviceStatus lastStatus) {
            if (lastStatus == null) {
                return toString();
            }
            ArrayList<String> list = new ArrayList<>();
            if (isFrontInfrared1() != lastStatus.isFrontInfrared1()) {
                list.add(String.format("front infra1 changed: from %S to %S", lastStatus.isFrontInfrared1(), isFrontInfrared1()));
            }
            if (isFrontInfrared2() != lastStatus.isFrontInfrared2()) {
                list.add(String.format("front infra2 changed: from %S to %S", lastStatus.isFrontInfrared2(), isFrontInfrared2()));
            }
            if (isBackInfrared2() != lastStatus.isBackInfrared2()) {
                list.add(String.format("back infra2 changed: from %S to %S", lastStatus.isBackInfrared2(), isBackInfrared2()));
            }
            if (isBackInfrared1() != lastStatus.isBackInfrared1()) {
                list.add(String.format("back infra1 changed: from %S to %S", lastStatus.isBackInfrared1(), isBackInfrared1()));
            }
            if (isButtonPressed() != lastStatus.isButtonPressed()) {
                list.add(String.format("button changed: from %S to %S", lastStatus.isButtonPressed(), isButtonPressed()));
            }
            return String.join(";", list);
        }

        // 红外1和红外4 遮挡为true，红外2和红外3遮挡为false,返回值统一为遮挡为true
        public boolean isFrontInfrared1() {
            return discreteInputs[WeightContext.front.getInfrared1()];
        }

        public boolean isFrontInfrared2() {
            return discreteInputs[WeightContext.front.getInfrared2()];
        }

        public boolean isBackInfrared2() {
            return discreteInputs[WeightContext.back.getInfrared2()];
        }

        public boolean isBackInfrared1() {
            return discreteInputs[WeightContext.back.getInfrared1()];
        }

        public boolean isButtonPressed() {
            return discreteInputs[WeightContext.front.getPrintButton()];
        }
    }
}
