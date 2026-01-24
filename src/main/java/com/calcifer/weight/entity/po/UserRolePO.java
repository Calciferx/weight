package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class UserRolePO {
    @TableId
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
