package com.calcifer.weight.repository;

import com.calcifer.weight.entity.po.UserRolePO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleMapper {
    Integer delete(String userId);

    List<UserRolePO> queryUserRole(UserRolePO userRolePO);

    Integer addBatch(UserRolePO userRolePO);
}
