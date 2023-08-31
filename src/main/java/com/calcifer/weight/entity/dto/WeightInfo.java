package com.calcifer.weight.entity.dto;

import lombok.Data;

@Data
public class WeightInfo {
    private String status;
    private String dataHex;

    public WeightInfo() {
    }

    public WeightInfo(String status, String dataHex) {
        this.status = status;
        this.dataHex = dataHex;
    }
}
