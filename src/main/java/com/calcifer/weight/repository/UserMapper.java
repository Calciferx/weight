package com.calcifer.weight.repository;

import com.calcifer.weight.entity.po.UserPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    Integer addUser(UserPO userPO);

    UserPO queryUser(UserPO userPO);

    List<UserPO> queryUserByIds(List<String> ids);

    Integer update(UserPO userPO);

    Integer delete(String id);
}
