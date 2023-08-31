package com.calcifer.weight.entity.po;

import lombok.Data;

@Data
public class UserRolePO {
    private String id;
    private String roleId;
    private String userId;

    public UserRolePO() {
    }

    public UserRolePO(String id, String roleId, String userId) {
        this.id = id;
        this.roleId = roleId;
        this.userId = userId;
    }
}
