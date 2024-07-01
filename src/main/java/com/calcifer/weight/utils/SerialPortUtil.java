package com.calcifer.weight.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SerialPortUtil {
    //查找所有可用端口
    public static List<String> findPorts() {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        // 返回去重后的端口名
        return Arrays.stream(serialPorts).map(SerialPort::getSystemPortName).distinct().collect(Collectors.toList());
    }

    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudRate 波特率
     * @return 串口对象
     */
    public static SerialPort openPort(String portName, int baudRate, int dataBit) {
        SerialPort serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        if (!serialPort.isOpen()) {    //开启串口
            serialPort.openPort(1000);
        } else {
            return serialPort;
        }
        // 设置一下串口的波特率等参数
        // 数据位：8
        // 停止位：1
        // 校验位：None
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortParameters(baudRate, dataBit, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
        return serialPort;
    }

    /**
     * 关闭串口
     *
     * @param serialPort 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    /**
     * 往串口发送数据
     *
     * @param serialPort 串口对象
     * @param content    待发送数据
     */
    public static void sendToPort(SerialPort serialPort, byte[] content) {
        if (!serialPort.isOpen()) {
            return;
        }
        serialPort.writeBytes(content, content.length);
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     */
    public static byte[] readFromPort(SerialPort serialPort) {
        byte[] reslutData = null;
        try {
            if (!serialPort.isOpen()) {
                return null;
            }

            int i = 0;
            while (serialPort.bytesAvailable() > 0 && i++ < 2) Thread.sleep(20);
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if (numRead > 0) {
                reslutData = readBuffer;
            }
        } catch (InterruptedException e) {
            log.warn("sleep interrupted", e);
        }
        return reslutData;
    }

    /**
     * 添加监听器
     *
     * @param serialPort 串口对象
     * @param listener   串口存在有效数据监听
     */
    public static void addListener(SerialPort serialPort, DataAvailableListener listener) {
        try {
            // 给串口添加监听器
            serialPort.addDataListener(new SerialPortListener(listener));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 串口监听
     */
    @Slf4j
    public static class SerialPortListener implements SerialPortDataListener {
        private DataAvailableListener mDataAvailableListener;

        public SerialPortListener(DataAvailableListener mDataAvailableListener) {
            this.mDataAvailableListener = mDataAvailableListener;
        }

        @Override
        public int getListeningEvents() {  //必须是return这个才会开启串口工具的监听
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (mDataAvailableListener != null) {
                mDataAvailableListener.dataAvailable(serialPortEvent);
            }
        }
    }

    /**
     * 串口存在有效数据监听
     */
    public interface DataAvailableListener {
        /**
         * 串口存在有效数据
         */
        void dataAvailable(SerialPortEvent serialPortEvent);
    }
}
