package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
@TableName("wg_slave_detail")
public class SlaveDetailPO {
    @TableId
    private String id;
    private String type;
    @JsonAlias("serial_name")
    private String serialName;
    @JsonAlias("create_time")
    private String createTime;
    @JsonAlias("update_time")
    private String updateTime;
    @JsonAlias("serial_sort")
    private Integer serialSort;
    @JsonAlias("slave_id")
    private String slaveId;
    private String status;
}
