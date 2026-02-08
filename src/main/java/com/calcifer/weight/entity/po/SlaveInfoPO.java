package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
@TableName("wg_slave_info")
public class SlaveInfoPO {
    @TableId
    private String id;
    @JsonAlias("slave_ip")
    private String slaveIp;

    @JsonAlias("slave_name")
    private String slaveName;

    @JsonAlias("slave_code")
    private String slaveCode;

    @JsonAlias("coil_name")
    private String coilName;

    @JsonAlias("coil_num")
    private int coilNum;

    @JsonAlias("discrete_name")
    private String discreteName;

    @JsonAlias("discrete_num")
    private int discreteNum;

    @JsonAlias("create_time")
    private String createTime;
    private String remark;
    private String status;
}
