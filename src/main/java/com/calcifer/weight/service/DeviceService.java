package com.calcifer.weight.service;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.calcifer.weight.entity.enums.WSCodeEnum.*;

/**
 * 串口和ModBus设备控制
 */
@Service
@Slf4j
public class DeviceService {
    public static int INFRA_FRONT_FOUND = 0;
    public static int INFRA_FRONT_WEIGH = 1;
    public static int BARRIER_FRONT_ON = 2;
    public static int BARRIER_FRONT_OFF = 3;
    public static int LIGHT_FRONT = 4;
    public static int LIGHT_BACK = 5;
    public static int BARRIER_BACK_OFF = 6;
    public static int BARRIER_BACK_ON = 7;
    public static int INFRA_BACK_WEIGH = 8;
    public static int INFRA_BACK_FOUND = 9;

    private SlaveInfo slaveInfo;
    private SlaveDetailInfo[] slaveDetailInfos;
    private SlaveDetailInfo[] slaveDetailInfosPositive;
    private SlaveDetailInfo[] slaveDetailInfosNegative;
    private ModbusMaster modbusMaster;

    @Resource(name = "cardListener")
    private SerialPortUtil.DataAvailableListener cardListener;

    @Resource(name = "scaleListener")
    private SerialPortUtil.DataAvailableListener scaleListener;
    private SerialPort scaleSerialPort;
    private SerialPort frontSerialPort;
    private SerialPort backSerialPort;


    @Value("${calcifer.weight.slave-ip}")
    private String slaveIp;

    @Value("${calcifer.weight.scale-port}")
    private String scalePort;

    @Value("${calcifer.weight.front-card-reader-port}")
    private String frontCardReaderPort;

    @Value("${calcifer.weight.back-card-reader-port}")
    private String backCardReaderPort;

    @Autowired
    private SlaveInfoService slaveInfoService;

    @Autowired
    private SlaveDetailService slaveDetailService;


    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @Value("${calcifer.weight.enable-modbus-device-init: true}")
    private boolean enableModbusDeviceInit;

    @Value("${calcifer.weight.enable-serial-device-init: true}")
    private boolean enableSerialDeviceInit;

    @Getter
    private ModBusDeviceStatus lastModBusDeviceStatus;

    @Getter
    private boolean init;
    //当前设备顺序，true为反转
    private boolean reverseFlag;


    public void init() {
        log.info("init devices...");
        if (enableModbusDeviceInit) {
            initModbusDevice();
        } else {
            log.info("enableDeviceInit is false, init end.");
        }
        if (enableSerialDeviceInit) {
            initSerialDevice();
        } else {
            log.info("initSerialDevice is false, init end.");
        }
        init = true;
    }

    private void initSerialDevice() {
        // 打开串口-称
        log.info("find and open weight's serial ports");
        List<String> ports = SerialPortUtil.findPorts();
        if (!ports.contains(scalePort)) {
            throw new RuntimeException("scale port: " + scalePort + " not exist!");
        }
        scaleSerialPort = SerialPortUtil.openPort(scalePort, 4800, 7);
        SerialPortUtil.addListener(scaleSerialPort, scaleListener);
        // 打开串口-前读卡器
        log.info("find and open front card's serial ports");
        if (!ports.contains(frontCardReaderPort)) {
            throw new RuntimeException("front card reader port: " + frontCardReaderPort + " not exist!");
        }
        frontSerialPort = SerialPortUtil.openPort(frontCardReaderPort, 57600, 8);
        SerialPortUtil.addListener(frontSerialPort, cardListener);
        // 打开串口-后读卡器
        log.info("find and open back card's serial ports");
        if (!ports.contains(backCardReaderPort)) {
            throw new RuntimeException("front card reader port: " + backCardReaderPort + " not exist!");
        }
        backSerialPort = SerialPortUtil.openPort(backCardReaderPort, 57600, 8);
        SerialPortUtil.addListener(backSerialPort, cardListener);
    }

    private void initModbusDevice() {
        slaveInfo = slaveInfoService.querySlaveInfoBySlaveIp(slaveIp);
        List<SlaveDetailInfo> slaveDetailInfoList = slaveDetailService.querySlaveDetailInfoBySlaveId(slaveInfo.getId());
        Map<String, SlaveDetailInfo> typeMap = slaveDetailInfoList.stream().collect(Collectors.toMap(SlaveDetailInfo::getType, Function.identity()));

        slaveDetailInfosPositive = new SlaveDetailInfo[10];
        slaveDetailInfosPositive[INFRA_FRONT_FOUND] = typeMap.get("1"); // infrared1
        slaveDetailInfosPositive[INFRA_FRONT_WEIGH] = typeMap.get("7"); // infrared2
        slaveDetailInfosPositive[BARRIER_FRONT_ON] = typeMap.get("2"); // barrierGate1On
        slaveDetailInfosPositive[BARRIER_FRONT_OFF] = typeMap.get("9"); // barrierGate1Off
        slaveDetailInfosPositive[LIGHT_FRONT] = typeMap.get("3"); // trafficLight1
        slaveDetailInfosPositive[LIGHT_BACK] = typeMap.get("6"); // trafficLight2
        slaveDetailInfosPositive[BARRIER_BACK_OFF] = typeMap.get("10"); // barrierGate2Off
        slaveDetailInfosPositive[BARRIER_BACK_ON] = typeMap.get("5"); // barrierGate2On
        slaveDetailInfosPositive[INFRA_BACK_WEIGH] = typeMap.get("4"); // infrared3
        slaveDetailInfosPositive[INFRA_BACK_FOUND] = typeMap.get("8"); // infrared4

        slaveDetailInfosNegative = slaveDetailInfosPositive.clone();
        ArrayUtil.reverse(slaveDetailInfosNegative);

        slaveDetailInfos = slaveDetailInfosPositive;


        initModbusMaster();
        // 关闭道闸 ON先置为false解控，OFF置为true受控，再OFF置为false解除控制
        log.info("closing barriers...");
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);

        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);

        // 红绿灯置为绿
        log.info("set all light green...");
        controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
    }

    @PreDestroy
    public void destroy() throws ModbusIOException {
        log.info("destroy...close serial ports and disconnect modbus devices");
        // 串口
        if (scaleSerialPort != null) {
            log.info("close \"scale\" serial port...");
            SerialPortUtil.closePort(scaleSerialPort);
        }
        if (frontSerialPort != null) {
            log.info("close \"front\" serial port...");
            SerialPortUtil.closePort(frontSerialPort);
        }
        if (backSerialPort != null) {
            log.info("close \"back\" serial port...");
            SerialPortUtil.closePort(backSerialPort);
        }
        // modbus
        if (modbusMaster != null) {
            log.info("modbusMaster is not null, disconnecting modbus devices");
            modbusMaster.disconnect();
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 10, backoff = @Backoff(delay = 100, multiplier = 2))
    public void controlModBusDevice(ModBusDeviceEnum modBusDeviceEnum, boolean status) {
//        if (true) return;
        log.info("control modbus device: {}, status: {}", modBusDeviceEnum.getMsg(), status);
        SlaveDetailInfo slaveDetailInfo = slaveDetailInfos[modBusDeviceEnum.getCode()];
        try {
            modbusMaster.writeSingleCoil(1, slaveDetailInfo.getSerialSort(), status);
        } catch (ModbusProtocolException e) {
            log.info("ModbusProtocolException: {}", e.getMessage());
            throw new RuntimeException("ModbusProtocolException: " + e.getMessage());
        } catch (ModbusNumberException e) {
            log.info("ModbusNumberException: {}", e.getMessage());
            throw new RuntimeException("ModbusNumberException: " + e.getMessage());
        } catch (ModbusIOException e) {
            log.info("ModbusIOException: {}", e.getMessage());
            throw new RuntimeException("ModbusIOException: " + e.getMessage());
        }
    }

    @Recover
    public void recover(Exception e) {
        log.error("RETRY FAILED！");
    }

    public void controlModBusDevice(Integer sort, boolean status) {
//        if (true) return;
        if (sort == null) return;
        try {
            log.info("write single coil sort: {}, status: {}", sort, status);
            modbusMaster.writeSingleCoil(1, sort, status);
        } catch (ModbusProtocolException e) {
            log.info("ModbusProtocolException: {}", e.getMessage());
            throw new RuntimeException("ModbusProtocolException: " + e.getMessage());
        } catch (ModbusNumberException e) {
            log.info("ModbusNumberException: {}", e.getMessage());
            throw new RuntimeException("ModbusNumberException: " + e.getMessage());
        } catch (ModbusIOException e) {
            log.info("ModbusIOException: {}", e.getMessage());
            throw new RuntimeException("ModbusIOException: " + e.getMessage());
        }
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
     * 设置进车方向
     */
    public void reverseDirection(boolean isReverse) {
        if (isReverse) {
            log.info("reverse direction...");
            // 设备顺序反转
            reverseFlag = !reverseFlag;
            slaveDetailInfos = reverseFlag ? slaveDetailInfosNegative : slaveDetailInfosPositive;
        }
    }

    /**
     * 读取ModBus设备状态（红外、红绿灯、道闸）
     */
    public ModBusDeviceStatus readModBusDeviceStatus() throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
        int slaveAddress = 1;
        int offset = 0;
        int quantity = slaveInfo.getCoilNum();
        boolean[] discreteInputs = modbusMaster.readDiscreteInputs(slaveAddress, offset, quantity);
        ModBusDeviceStatus modBusDeviceStatus = new ModBusDeviceStatus(discreteInputs);
        // 发送红外状态的ws消息
        webSocketHandler.sendJsonToAllUser(new WSRespWrapper<>(Map.of("infra1" ,modBusDeviceStatus.isInfrared1(), "infra2" ,modBusDeviceStatus.isInfrared2(), "infra3" ,modBusDeviceStatus.isInfrared3(), "infra4" ,modBusDeviceStatus.isInfrared4()), INFRA));
        String statusChangeStr = modBusDeviceStatus.getStatusChangeStr(lastModBusDeviceStatus);
        if (StringUtils.hasLength(statusChangeStr)) {
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
            return String.format("device status: %s, InfraredFrontFound:%s, InfraredFrontWeigh:%s, InfraredBackWeigh:%s, InfraredBackFound:%s", JSON.toJSONString(discreteInputs), isInfraredFrontFound(), isInfraredFrontWeigh(), isInfraredBackWeigh(), isInfraredBackFound());
        }

        public String getStatusChangeStr(ModBusDeviceStatus lastStatus) {
            if (lastStatus == null) {
                return toString();
            }
            ArrayList<String> list = new ArrayList<>();
            if (isInfraredFrontFound() != lastStatus.isInfraredFrontFound()) {
                list.add(String.format("InfraredFrontFound changed: from %S to %S", lastStatus.isInfraredFrontFound(), isInfraredFrontFound()));
            }
            if (isInfraredFrontWeigh() != lastStatus.isInfraredFrontWeigh()) {
                list.add(String.format("InfraredFrontWeigh changed: from %S to %S", lastStatus.isInfraredFrontWeigh(), isInfraredFrontWeigh()));
            }
            if (isInfraredBackWeigh() != lastStatus.isInfraredBackWeigh()) {
                list.add(String.format("InfraredBackWeigh changed: from %S to %S", lastStatus.isInfraredBackWeigh(), isInfraredBackWeigh()));
            }
            if (isInfraredBackFound() != lastStatus.isInfraredBackFound()) {
                list.add(String.format("InfraredBackFound changed: from %S to %S", lastStatus.isInfraredBackFound(), isInfraredBackFound()));
            }
            return String.join(";", list);
        }

        // 车检红外遮挡为true，计量红外遮挡为false,返回值统一为遮挡为true
        public boolean isInfraredFrontFound() {
            return discreteInputs[slaveDetailInfos[INFRA_FRONT_FOUND].getSerialSort()];
        }

        public boolean isInfraredFrontWeigh() {
            return !discreteInputs[slaveDetailInfos[INFRA_FRONT_WEIGH].getSerialSort()];
        }

        public boolean isInfraredBackWeigh() {
            return !discreteInputs[slaveDetailInfos[INFRA_BACK_WEIGH].getSerialSort()];
        }

        public boolean isInfraredBackFound() {
            return discreteInputs[slaveDetailInfos[INFRA_BACK_FOUND].getSerialSort()];
        }

        // 固定设备顺序，不随reverse而变
        // 车检红外遮挡为true，计量红外遮挡为false,返回值统一为遮挡为true
        public boolean isInfrared1() {
            return discreteInputs[slaveDetailInfosPositive[INFRA_FRONT_FOUND].getSerialSort()];
        }

        public boolean isInfrared2() {
            return !discreteInputs[slaveDetailInfosPositive[INFRA_FRONT_WEIGH].getSerialSort()];
        }

        public boolean isInfrared3() {
            return !discreteInputs[slaveDetailInfosPositive[INFRA_BACK_WEIGH].getSerialSort()];
        }

        public boolean isInfrared4() {
            return discreteInputs[slaveDetailInfosPositive[INFRA_BACK_FOUND].getSerialSort()];
        }
    }
}
