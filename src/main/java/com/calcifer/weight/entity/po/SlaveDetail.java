package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wg_slave_detail")
public class SlaveDetail {
    @TableId
    private String id;
    private String type;
    private String serialName;
    private String createTime;
    private String updateTime;
    private Integer serialSort;
    private String slaveId;
    private String status;
}
