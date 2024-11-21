package com.calcifer.weight.service;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.domain.SerialDeviceInfo;
import com.calcifer.weight.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class SerialDeviceService {
    @Value("${calcifer.weight.enable-serial-device-init: true}")
    private boolean enableSerialDeviceInit;
    @Value("${calcifer.weight.serial-device-info-path}")
    private String serialDeviceInfoPath;
    @Getter
    @Resource(name = "cardListener")
    private SerialPortUtil.DataAvailableListener cardListener;

    @Getter
    @Resource(name = "scaleListener")
    private SerialPortUtil.DataAvailableListener scaleListener;
    private List<SerialPort> serialPortList;

    @Getter
    private boolean init;

    public void init() {
        log.info("init serial devices...");
        if (enableSerialDeviceInit) {
            initSerialDevice();
        } else {
            log.info("initSerialDevice is false, init end.");
        }
        init = true;
    }

    private void initSerialDevice() {
        String serialDeviceInfoJson = FileUtil.readUtf8String(serialDeviceInfoPath);
        List<SerialDeviceInfo> serialDeviceInfos = JSON.parseArray(serialDeviceInfoJson, SerialDeviceInfo.class);
        // 打开串口-称
        log.info("find and open serial ports");
        List<String> ports = SerialPortUtil.findPorts();
        for (SerialDeviceInfo info : serialDeviceInfos) {
            String port = info.getPort();
            if (!ports.contains(port)) {
                throw new RuntimeException("scale port: " + port + " not exist!");
            }
            SerialPort serialPort = SerialPortUtil.openPort(port, info.getBaudRate(), info.getDataBit());
            serialPortList.add(serialPort);
            if (info.getType().equals("scale")) {
                SerialPortUtil.addListener(serialPort, scaleListener);
            } else {
                SerialPortUtil.addListener(serialPort, cardListener);
            }
        }

    }

    @PreDestroy
    public void destroy() throws ModbusIOException {
        log.info("destroy...close serial ports");
        // 串口
        for (SerialPort serialPort : serialPortList) {
            if (serialPort != null) {
                log.info("close serial port {}...", serialPort.getSystemPortPath());
                SerialPortUtil.closePort(serialPort);
            }
        }
    }
}
