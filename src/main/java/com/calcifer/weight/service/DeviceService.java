package com.calcifer.weight.service;

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
    private SlaveInfo slaveInfo;
    private SlaveDetailInfo[] slaveDetailInfos;
    private WSMessageTypeEnum[] wsMessageType;
    private int[] wsMessageTypeIndex;
    private ModbusMaster modbusMaster;

    @Resource(name = "cardListener")
    private SerialPortUtil.DataAvailableListener cardListener;

    @Resource(name = "scaleListener")
    private SerialPortUtil.DataAvailableListener scaleListener;
    private SerialPort scaleSerialPort;
    private SerialPort frontSerialPort;
    private SerialPort backSerialPort;

    public SerialPortUtil.DataAvailableListener getCardListener() {
        return cardListener;
    }

    public SerialPortUtil.DataAvailableListener getScaleListener() {
        return scaleListener;
    }


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

    @Value("${calcifer.weight.enable-device-init: true}")
    private boolean enableDeviceInit;

    @PostConstruct
    public void init() {
        if (!enableDeviceInit) return;
        slaveInfo = slaveInfoService.querySlaveInfoBySlaveIp(slaveIp);
        List<SlaveDetailInfo> slaveDetailInfoList = slaveDetailService.querySlaveDetailInfoBySlaveId(slaveInfo.getId());
        Map<String, SlaveDetailInfo> typeMap = slaveDetailInfoList.stream().collect(Collectors.toMap(SlaveDetailInfo::getType, Function.identity()));

        wsMessageType = new WSMessageTypeEnum[]{INFR_NORTH_OUT, INFR_NORTH_IN, INFR_SOUTH_IN, INFR_SOUTH_OUT};
        wsMessageTypeIndex = new int[]{0, 1, 2, 3};

        slaveDetailInfos = new SlaveDetailInfo[10];
        slaveDetailInfos[0] = typeMap.get("1"); // infrared1
        slaveDetailInfos[1] = typeMap.get("7"); // infrared2
        slaveDetailInfos[2] = typeMap.get("2"); // barrierGate1On
        slaveDetailInfos[3] = typeMap.get("9"); // barrierGate1Off
        slaveDetailInfos[4] = typeMap.get("3"); // trafficLight1
        slaveDetailInfos[5] = typeMap.get("6"); // trafficLight2
        slaveDetailInfos[6] = typeMap.get("10"); // barrierGate2Off
        slaveDetailInfos[7] = typeMap.get("5"); // barrierGate2On
        slaveDetailInfos[8] = typeMap.get("4"); // infrared3
        slaveDetailInfos[9] = typeMap.get("8"); // infrared4

        initModbusMaster();
        // 关闭道闸 ON先置为false解控，OFF置为true受控，再OFF置为false解除控制
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
        controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
        controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
        // 打开串口-称
        List<String> ports = SerialPortUtil.findPorts();
        if (!ports.contains(scalePort)) {
            throw new RuntimeException("scale port: " + scalePort + " not exist!");
        }
        scaleSerialPort = SerialPortUtil.openPort(scalePort, 4800, 7);
        SerialPortUtil.addListener(scaleSerialPort, scaleListener);
        // 打开串口-前读卡器
        if (!ports.contains(frontCardReaderPort)) {
            throw new RuntimeException("front card reader port: " + frontCardReaderPort + " not exist!");
        }
        frontSerialPort = SerialPortUtil.openPort(frontCardReaderPort, 57600, 7);
        SerialPortUtil.addListener(frontSerialPort, cardListener);
        // 打开串口-后读卡器
        if (!ports.contains(backCardReaderPort)) {
            throw new RuntimeException("front card reader port: " + backCardReaderPort + " not exist!");
        }
        backSerialPort = SerialPortUtil.openPort(backCardReaderPort, 57600, 7);
        SerialPortUtil.addListener(backSerialPort, cardListener);
    }

    @PreDestroy
    public void destroy() throws ModbusIOException {
        // 串口
        SerialPortUtil.closePort(scaleSerialPort);
        SerialPortUtil.closePort(frontSerialPort);
        SerialPortUtil.closePort(backSerialPort);
        // modbus
        modbusMaster.disconnect();
    }

    public void controlModBusDevice(ModBusDeviceEnum modBusDeviceEnum, boolean status) {
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
        if (sort == null) return;
        try {
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
        return new ModBusDeviceStatus(discreteInputs);
    }

    public class ModBusDeviceStatus {
        private final boolean[] discreteInputs;

        private ModBusDeviceStatus(boolean[] discreteInputs) {
            this.discreteInputs = discreteInputs;
        }


        public boolean isInfrared1() {
            return discreteInputs[slaveDetailInfos[0].getSerialSort()];
        }

        public boolean isInfrared2() {
            return discreteInputs[slaveDetailInfos[1].getSerialSort()];
        }

        public boolean isInfrared3() {
            return discreteInputs[slaveDetailInfos[6].getSerialSort()];
        }

        public boolean isInfrared4() {
            return discreteInputs[slaveDetailInfos[7].getSerialSort()];
        }

        public boolean isTrafficLight1() {
            return discreteInputs[slaveDetailInfos[4].getSerialSort()];
        }

        public boolean isTrafficLight2() {
            return discreteInputs[slaveDetailInfos[5].getSerialSort()];
        }
    }
}
