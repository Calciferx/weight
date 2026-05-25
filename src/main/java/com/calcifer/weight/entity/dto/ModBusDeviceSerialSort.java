package com.calcifer.weight.entity.dto;

import lombok.Data;

@Data
public class ModBusDeviceSerialSort {
    private int infrared1;
    private int infrared2;
    private int barrierGateOn;
    private int barrierGateOff;
    private int redLight;
    private int greenLight;
    private int printButton;
    private String plateReaderIP;
}
