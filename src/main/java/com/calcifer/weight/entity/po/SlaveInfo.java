package com.calcifer.weight.entity.po;

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

    public SlaveInfo(String slaveIp, String id, String status) {
        this.slaveIp = slaveIp;
        this.status = status;
        this.id = id;
    }

    public SlaveInfo(String id, String slaveIp, String slaveName, String slaveCode, String coilName, int coilNum, String discreteName, int discreteNum, String createTime, String remark, String status) {
        this.id = id;
        this.slaveIp = slaveIp;
        this.slaveName = slaveName;
        this.slaveCode = slaveCode;
        this.coilName = coilName;
        this.coilNum = coilNum;
        this.discreteName = discreteName;
        this.discreteNum = discreteNum;
        this.createTime = createTime;
        this.remark = remark;
        this.status = status;
    }
}
