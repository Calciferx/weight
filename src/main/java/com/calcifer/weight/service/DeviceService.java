package com.calcifer.weight.service;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.WSMessageTypeEnum;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.CardMapper;
import com.calcifer.weight.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.calcifer.weight.entity.enums.WSMessageTypeEnum.*;

/**
 * 串口和ModBus设备控制
 */
@Service
@Slf4j
public class DeviceService {
    public static int INFRA1 = 0;
    public static int INFRA2 = 1;
    public static int BARRIER1_ON = 2;
    public static int BARRIER1_OFF = 3;
    public static int LIGHT1 = 4;
    public static int LIGHT2 = 5;
    public static int BARRIER2_OFF = 6;
    public static int BARRIER2_ON = 7;
    public static int INFRA3 = 8;
    public static int INFRA4 = 9;

    private SlaveInfo slaveInfo;
    private SlaveDetailInfo[] slaveDetailInfos;
    private WSMessageTypeEnum[] wsMessageType;
    private int[] wsMessageTypeIndex;
    private ModbusMaster modbusMaster;

    @Getter
    @Resource(name = "cardListener")
    private SerialPortUtil.DataAvailableListener cardListener;

    @Getter
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
    private CardMapper cardMapper;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @Value("${calcifer.weight.enable-modbus-device-init: true}")
    private boolean enableModbusDeviceInit;

    @Value("${calcifer.weight.enable-serial-device-init: true}")
    private boolean enableSerialDeviceInit;

    private boolean[] deviceStatusArray;

    @PostConstruct
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

        wsMessageType = new WSMessageTypeEnum[]{INFR_NORTH_OUT, INFR_NORTH_IN, INFR_SOUTH_IN, INFR_SOUTH_OUT};
        wsMessageTypeIndex = new int[]{0, 1, 2, 3};

        slaveDetailInfos = new SlaveDetailInfo[10];
        slaveDetailInfos[INFRA1] = typeMap.get("1"); // infrared1
        slaveDetailInfos[INFRA2] = typeMap.get("7"); // infrared2
        slaveDetailInfos[BARRIER1_ON] = typeMap.get("2"); // barrierGate1On
        slaveDetailInfos[BARRIER1_OFF] = typeMap.get("9"); // barrierGate1Off
        slaveDetailInfos[LIGHT1] = typeMap.get("3"); // trafficLight1
        slaveDetailInfos[LIGHT2] = typeMap.get("6"); // trafficLight2
        slaveDetailInfos[BARRIER2_OFF] = typeMap.get("10"); // barrierGate2Off
        slaveDetailInfos[BARRIER2_ON] = typeMap.get("5"); // barrierGate2On
        slaveDetailInfos[INFRA3] = typeMap.get("4"); // infrared3
        slaveDetailInfos[INFRA4] = typeMap.get("8"); // infrared4


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

    public void controlModBusDevice(ModBusDeviceEnum modBusDeviceEnum, boolean status) {
//        if (true) return;
        log.info("control modbus device: {}, status: {}",modBusDeviceEnum.getMsg(), status);
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
            // 反转数组
            SlaveDetailInfo tmp;
            for (int i = slaveDetailInfos.length / 2 - 1; i > -1; i--) {
                tmp = slaveDetailInfos[i];
                slaveDetailInfos[i] = slaveDetailInfos[7 - i];
                slaveDetailInfos[7 - i] = tmp;
            }
            for (int i = wsMessageTypeIndex.length - 1; i > -1; i--) {
                wsMessageTypeIndex[i] = wsMessageTypeIndex.length - 1 - wsMessageTypeIndex[i];
            }
        }
    }

    public WSMessageTypeEnum getInfr1MessageTypeEnum() {
        return wsMessageType[wsMessageTypeIndex[0]];
    }

    public WSMessageTypeEnum getInfr2MessageTypeEnum() {
        return wsMessageType[wsMessageTypeIndex[1]];
    }

    public WSMessageTypeEnum getInfr3MessageTypeEnum() {
        return wsMessageType[wsMessageTypeIndex[2]];
    }

    public WSMessageTypeEnum getInfr4MessageTypeEnum() {
        return wsMessageType[wsMessageTypeIndex[3]];
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
        log.info(modBusDeviceStatus.toString());
        return modBusDeviceStatus;
    }

    public class ModBusDeviceStatus {
        private final boolean[] discreteInputs;

        private ModBusDeviceStatus(boolean[] discreteInputs) {
            this.discreteInputs = discreteInputs;
        }

        @Override
        public String toString() {
            return String.format("device status: %s, infra1:%s, infra2:%s, infra3:%s, infra4:%s", JSON.toJSONString(discreteInputs), isInfrared1(), isInfrared2(), isInfrared3(), isInfrared4());
        }

        // 红外1和红外4 遮挡为true，红外2和红外3遮挡为false
        public boolean isInfrared1() {
            return discreteInputs[slaveDetailInfos[INFRA1].getSerialSort()];
        }

        public boolean isInfrared2() {
            return !discreteInputs[slaveDetailInfos[INFRA2].getSerialSort()];
        }

        public boolean isInfrared3() {
            return !discreteInputs[slaveDetailInfos[INFRA3].getSerialSort()];
        }

        public boolean isInfrared4() {
            return discreteInputs[slaveDetailInfos[INFRA4].getSerialSort()];
        }

        public boolean isTrafficLight1() {
            return discreteInputs[slaveDetailInfos[LIGHT1].getSerialSort()];
        }

        public boolean isTrafficLight2() {
            return discreteInputs[slaveDetailInfos[LIGHT2].getSerialSort()];
        }
    }
}
