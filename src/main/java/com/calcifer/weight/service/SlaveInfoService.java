package com.calcifer.weight.service;

import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.repository.SlaveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaveInfoService {
    @Autowired
    private SlaveMapper slaveMapper;

    public SlaveInfo querySlaveInfoBySlaveIp(String slaveIp) {
        SlaveInfo slaveInfo = new SlaveInfo();
        slaveInfo.setSlaveIp(slaveIp);
        slaveInfo.setStatus("1");
        List<SlaveInfo> slaveInfos = slaveMapper.querySlaveInfo(slaveInfo);
        if (slaveInfos.isEmpty()) {
            return null;
        } else {
            return slaveInfos.get(0);
        }
    }

    public List<SlaveInfo> querySlaveInfo(String slaveIp, String id, String status, String keywords) {
        SlaveInfo slaveInfo = new SlaveInfo(slaveIp, id, "1");
        slaveInfo.setKeywords(keywords);
        return slaveMapper.querySlaveInfo(slaveInfo);
    }

    public Integer count(SlaveInfo slaveInfo) {
        return slaveMapper.count(slaveInfo);
    }

    public Integer count(String slaveIp, String id, String status) {
        return slaveMapper.count(new SlaveInfo(slaveIp, id, status));
    }

    public Integer add(SlaveInfo slaveInfo) {
        return slaveMapper.add(slaveInfo);
    }

    public Integer update(SlaveInfo slaveInfo) {
        return slaveMapper.update(slaveInfo);
    }

    public Integer delete(String id) {
        SlaveInfo slaveInfo = new SlaveInfo();
        slaveInfo.setId(id);
        return slaveMapper.delete(slaveInfo);
    }
}
