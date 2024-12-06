package com.calcifer.weight.entity.dto;

import lombok.Data;

@Data
public class ScaleDataDTO {
    private byte[] originData;
    private String originDataHex;
    private int type;

    private int weightNum;
    private String status;

    public ScaleDataDTO(byte[] originData, int type) {
        this.originData = originData;

        if (type == 1) {

        }
    }
}
