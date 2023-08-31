package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Data
public class User {
    private String id;
    @JsonIgnore
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
}
