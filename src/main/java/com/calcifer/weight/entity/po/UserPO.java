package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("sys_user")
public class UserPO {
    @TableId("id")
    private String userId;
    @TableField("name")
    private String username;
    @TableField("pwd")
    private String password;
    private UserStatusEnum status;
    private String createTime;
    private String realName;
    @TableField("phone")
    private String phoneNumber;
    @TableField("role_id")
    private String role;
    private String customerStatus;
    private String areaId;
    private String managerCustomer;
}
