package com.calcifer.weight.service;

import com.calcifer.weight.entity.po.UserSlaveInfo;
import com.calcifer.weight.repository.UserSlaveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSlaveService {
    @Autowired
    private UserSlaveMapper userSlaveMapper;

    public UserSlaveInfo queryUserSlaveInfo(String userId) {
        List<UserSlaveInfo> userSlaveInfoList = userSlaveMapper.queryUserSlaveInfo(null);
        if (userSlaveInfoList != null && !userSlaveInfoList.isEmpty()) {
            return userSlaveInfoList.get(0);
        }
        return null;
    }
}
