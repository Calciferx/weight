package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wg_user_slave")
public class UserSlave {
    @TableId
    private String id;
    private String slaveId;
    private String userId;
    private String createTime;
}
