package com.calcifer.weight.entity.domain;

import lombok.Data;

@Data
public class SerialDeviceInfo {
    private String type;
    private String port;
    private Integer baudRate;
    private Integer dataBit;
}
