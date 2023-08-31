package com.calcifer.weight.entity.po;

import com.calcifer.weight.entity.enums.UserStatusEnum;
import lombok.Data;

@Data
public class UserPO {
    private String id;
    private String pwd;
    private String name;
    private String phone;
    private UserStatusEnum status;
    private String createTime;
    private String realName;
    private String areaId;
    private String managerCustomer;
    private Integer customerStatus;
    private String roleId;

    private String roleName;

    public UserPO() {
    }

    public UserPO(String name) {
        this.name = name;
    }

    public UserPO(String id, String pwd, String name, String phone, UserStatusEnum status, String createTime, String realName, String areaId, String managerCustomer, Integer customerStatus, String roleId) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.createTime = createTime;
        this.realName = realName;
        this.areaId = areaId;
        this.managerCustomer = managerCustomer;
        this.customerStatus = customerStatus;
        this.roleId = roleId;
    }
}
