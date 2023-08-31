package com.calcifer.weight.service;


import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.repository.SlaveDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaveDetailService {

    @Autowired
    private SlaveDetailMapper slaveDetailMapper;

    public List<SlaveDetailInfo> querySlaveDetailInfo(String slaveId, String status, String slaveIp) {
        SlaveDetailInfo slaveDetailInfo = new SlaveDetailInfo(slaveId, status, slaveIp);
        return slaveDetailMapper.querySlaveDetailInfo(slaveDetailInfo);
    }

    public List<SlaveDetailInfo> querySlaveDetailInfoBySlaveId(String slaveId) {
        return querySlaveDetailInfo(slaveId, "1", null);
    }

    public Integer addDetails(SlaveDetailInfo slaveDetailInfo) {
        return slaveDetailMapper.addDetails(slaveDetailInfo);
    }

    public Integer delete(String slaveId) {
        SlaveDetailInfo slaveDetailInfo = new SlaveDetailInfo();
        slaveDetailInfo.setSlaveId(slaveId);
        return slaveDetailMapper.delete(slaveDetailInfo);
    }
}
