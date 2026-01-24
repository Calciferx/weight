package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.repository.SlaveMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaveInfoService extends ServiceImpl<SlaveMapper, SlaveInfo> {

    public SlaveInfo querySlaveInfoBySlaveIp(String slaveIp) {
        return lambdaQuery()
                .eq(SlaveInfo::getSlaveIp, slaveIp)
                .eq(SlaveInfo::getStatus, "1")
                .one();
    }

    public List<SlaveInfo> querySlaveInfo(String slaveIp, String id, String status, String keywords) {
        return lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveIp), SlaveInfo::getSlaveIp, slaveIp)
                .eq(StringUtils.isNotBlank(id), SlaveInfo::getId, id)
                .eq(StringUtils.isNotBlank(status), SlaveInfo::getStatus, status)
                .and(StringUtils.isNotBlank(keywords), w -> w.like(SlaveInfo::getSlaveIp, keywords)
                        .or().like(SlaveInfo::getSlaveName, keywords)
                        .or().like(SlaveInfo::getSlaveCode, keywords)
                        .or().like(SlaveInfo::getCoilName, keywords)
                        .or().like(SlaveInfo::getDiscreteName, keywords))
                .list();
    }

    public Integer count(SlaveInfo slaveInfo) {
        return Math.toIntExact(lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveInfo.getSlaveIp()), SlaveInfo::getSlaveIp, slaveInfo.getSlaveIp())
                .eq(StringUtils.isNotBlank(slaveInfo.getId()), SlaveInfo::getId, slaveInfo.getId())
                .eq(StringUtils.isNotBlank(slaveInfo.getStatus()), SlaveInfo::getStatus, slaveInfo.getStatus())
                .count());
    }

    public Integer count(String slaveIp, String id, String status) {
        return Math.toIntExact(lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveIp), SlaveInfo::getSlaveIp, slaveIp)
                .eq(StringUtils.isNotBlank(id), SlaveInfo::getId, id)
                .eq(StringUtils.isNotBlank(status), SlaveInfo::getStatus, status)
                .count());
    }

    public Integer add(SlaveInfo slaveInfo) {
        return save(slaveInfo) ? 1 : 0;
    }

    public Integer update(SlaveInfo slaveInfo) {
        return updateById(slaveInfo) ? 1 : 0;
    }

    public Integer delete(String id) {
        return lambdaUpdate()
                .eq(SlaveInfo::getId, id)
                .eq(SlaveInfo::getStatus, "2")
                .remove() ? 1 : 0;
    }
}
