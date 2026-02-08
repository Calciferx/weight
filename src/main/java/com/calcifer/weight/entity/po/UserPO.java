package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("sys_user")
public class UserPO {
    @TableId("id")
    @JsonAlias("id")
    private String userId;

    @TableField("name")
    @JsonAlias("name")
    private String username;

    @TableField("pwd")
    @JsonAlias("pwd")
    private String password;

    private UserStatusEnum status;

    @JsonAlias("create_time")
    private String createTime;

    @JsonAlias("real_name")
    private String realName;

    @TableField("phone")
    @JsonAlias("phone")
    private String phoneNumber;

    @TableField("role_id")
    @JsonAlias("role_id")
    private String role;

    @JsonAlias("customer_status")
    private String customerStatus;

    @JsonAlias("area_id")
    private String areaId;

    @JsonAlias("manager_customer")
    private String managerCustomer;
}
