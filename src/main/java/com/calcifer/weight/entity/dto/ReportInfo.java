package com.calcifer.weight.entity.dto;

import lombok.Data;

@Data
public class ReportInfo {
    private Double roughWeight;
    private Double tareWeight;
    private Double netWeight;
    private Integer carNum;
    private String goodsName;
    private String carNo;

    private String staticsType;
    private String startTime;
    private String endTime;
    private String weighingMode;

    public ReportInfo() {
    }

    public ReportInfo(String staticsType, String startTime, String endTime, String weighingMode) {
        this.staticsType = staticsType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weighingMode = weighingMode;
    }
}
