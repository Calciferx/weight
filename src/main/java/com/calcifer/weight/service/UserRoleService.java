package com.calcifer.weight.service;

import com.calcifer.weight.entity.domain.UserRolePO;
import com.calcifer.weight.repository.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    public Integer addRole(String userId, String roleId) {
        userRoleMapper.delete(userId);
        UserRolePO userRolePO = new UserRolePO(null, roleId, userId);
        return userRoleMapper.addBatch(userRolePO);
    }

    public List<UserRolePO> queryUserRole(String userId) {
        UserRolePO userRolePO = new UserRolePO(null, null, userId);
        return userRoleMapper.queryUserRole(userRolePO);
    }

    public List<UserRolePO> queryUserRole(String id, String roleId, String userId) {
        UserRolePO userRolePO = new UserRolePO(id, roleId, userId);
        return userRoleMapper.queryUserRole(userRolePO);
    }
}
