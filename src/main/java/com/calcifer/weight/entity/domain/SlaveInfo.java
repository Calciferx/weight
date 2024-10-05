package com.calcifer.weight.entity.domain;

import lombok.Data;

@Data
public class SlaveInfo {
    private String id;
    private String slaveIp;
    private String slaveName;
    private String slaveCode;
    private String coilName;
    private int coilNum;
    private String discreteName;
    private int discreteNum;
    private String createTime;
    private String remark;
    private String status;

    private String keywords;

    public SlaveInfo() {
    }
}
