package com.calcifer.weight.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.entity.po.UserSlave;
import com.calcifer.weight.entity.po.UserSlaveInfo;
import com.calcifer.weight.repository.SlaveMapper;
import com.calcifer.weight.repository.UserMapper;
import com.calcifer.weight.repository.UserSlaveMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserSlaveService extends ServiceImpl<UserSlaveMapper, UserSlave> {
    @Autowired
    private SlaveMapper slaveMapper;
    @Autowired
    private UserMapper userMapper;

    public List<UserSlaveInfo> queryUserSlaveInfo(String slaveId) {
        LambdaQueryWrapper<UserSlave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(slaveId), UserSlave::getSlaveId, slaveId);
        List<UserSlave> userSlaves = list(wrapper);

        List<UserSlaveInfo> result = new ArrayList<>();
        if (userSlaves.isEmpty()) {
            return result;
        }

        Set<String> slaveIds = userSlaves.stream().map(UserSlave::getSlaveId).collect(Collectors.toSet());
        Set<String> userIds = userSlaves.stream().map(UserSlave::getUserId).collect(Collectors.toSet());

        Map<String, SlaveInfo> slaveMap = slaveMapper.selectBatchIds(slaveIds).stream()
                .collect(Collectors.toMap(SlaveInfo::getId, s -> s));
        Map<String, UserPO> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UserPO::getUserId, u -> u));

        for (UserSlave us : userSlaves) {
            UserSlaveInfo info = new UserSlaveInfo();
            info.setId(us.getId());
            info.setSlaveId(us.getSlaveId());
            info.setUserId(us.getUserId());
            info.setCreateTime(us.getCreateTime());

            SlaveInfo slave = slaveMap.get(us.getSlaveId());
            if (slave != null) {
                info.setName(slave.getSlaveName());
                info.setSlaveCode(slave.getSlaveCode());
                info.setStatus(slave.getStatus());
            }

            UserPO user = userMap.get(us.getUserId());
            if (user != null) {
                info.setRealName(user.getRealName());
            }
            result.add(info);
        }
        return result;
    }
}
