package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.UserRolePO;
import com.calcifer.weight.repository.UserRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRolePO> {

    public Integer addRole(String userId, String roleId) {
        remove(lambdaQuery().eq(UserRolePO::getUserId, userId));
        UserRolePO userRolePO = new UserRolePO(null, roleId, userId);
        return save(userRolePO) ? 1 : 0;
    }

    public List<UserRolePO> queryUserRole(String userId) {
        UserRolePO userRolePO = new UserRolePO(null, null, userId);
        return this.queryUserRole(userRolePO);
    }

    public List<UserRolePO> queryUserRole(String id, String roleId, String userId) {
        UserRolePO userRolePO = new UserRolePO(id, roleId, userId);
        return this.queryUserRole(userRolePO);
    }

    private List<UserRolePO> queryUserRole(UserRolePO userRolePO) {
        return lambdaQuery()
                .eq(StringUtils.isNotBlank(userRolePO.getUserId()), UserRolePO::getUserId, userRolePO.getUserId())
                .eq(StringUtils.isNotBlank(userRolePO.getRoleId()), UserRolePO::getRoleId, userRolePO.getRoleId())
                .eq(StringUtils.isNotBlank(userRolePO.getId()), UserRolePO::getId, userRolePO.getId())
                .list();
    }
}
