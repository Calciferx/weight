package com.calcifer.weight.repository;

import com.calcifer.weight.entity.po.UserSlaveInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSlaveMapper {

    List<UserSlaveInfo> queryUserSlaveInfo(String slaveId);
}
