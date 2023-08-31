package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.UserStatusEnum;
import lombok.Data;

@Data
public class UserVO {
    private String id;
    private String name;
    private UserStatusEnum status;
    private String phone;
    private String createTime;
    private String realName;
    private Integer customerStatus;
    private String managerCustomer;
    private String roleId;
    private String areaId;
}
