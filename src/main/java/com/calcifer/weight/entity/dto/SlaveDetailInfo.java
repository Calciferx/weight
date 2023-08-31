package com.calcifer.weight.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlaveDetailInfo {
    private String id;
    private String type;
    private String serialName;
    private String createTime;
    private String updateTime;
    private int serialSort;
    private String slaveId;
    private String status;
    private String slaveIp;
    private String coilNum;
    private String discreteNum;

    public SlaveDetailInfo(String slaveId, String status) {
        this.slaveId = slaveId;
        this.status = status;
    }

    public SlaveDetailInfo(String slaveId, String status, String slaveIp) {
        this.slaveId = slaveId;
        this.status = status;
        this.slaveIp = slaveIp;
    }

    public SlaveDetailInfo(String createTime, String updateTime, String slaveId, String status) {
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.slaveId = slaveId;
        this.status = status;
    }
}
