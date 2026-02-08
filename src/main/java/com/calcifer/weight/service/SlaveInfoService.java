package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.SlaveInfoPO;
import com.calcifer.weight.repository.SlaveMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaveInfoService extends ServiceImpl<SlaveMapper, SlaveInfoPO> {

    public SlaveInfoPO querySlaveInfoBySlaveIp(String slaveIp) {
        return lambdaQuery()
                .eq(SlaveInfoPO::getSlaveIp, slaveIp)
                .eq(SlaveInfoPO::getStatus, "1")
                .one();
    }

    public List<SlaveInfoPO> querySlaveInfo(String slaveIp, String id, String status, String keywords) {
        return lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveIp), SlaveInfoPO::getSlaveIp, slaveIp)
                .eq(StringUtils.isNotBlank(id), SlaveInfoPO::getId, id)
                .eq(StringUtils.isNotBlank(status), SlaveInfoPO::getStatus, status)
                .and(StringUtils.isNotBlank(keywords), w -> w.like(SlaveInfoPO::getSlaveIp, keywords)
                        .or().like(SlaveInfoPO::getSlaveName, keywords)
                        .or().like(SlaveInfoPO::getSlaveCode, keywords)
                        .or().like(SlaveInfoPO::getCoilName, keywords)
                        .or().like(SlaveInfoPO::getDiscreteName, keywords))
                .list();
    }

    public Integer count(SlaveInfoPO slaveInfoPO) {
        return Math.toIntExact(lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveInfoPO.getSlaveIp()), SlaveInfoPO::getSlaveIp, slaveInfoPO.getSlaveIp())
                .eq(StringUtils.isNotBlank(slaveInfoPO.getId()), SlaveInfoPO::getId, slaveInfoPO.getId())
                .eq(StringUtils.isNotBlank(slaveInfoPO.getStatus()), SlaveInfoPO::getStatus, slaveInfoPO.getStatus())
                .count());
    }

    public Integer count(String slaveIp, String id, String status) {
        return Math.toIntExact(lambdaQuery()
                .eq(StringUtils.isNotBlank(slaveIp), SlaveInfoPO::getSlaveIp, slaveIp)
                .eq(StringUtils.isNotBlank(id), SlaveInfoPO::getId, id)
                .eq(StringUtils.isNotBlank(status), SlaveInfoPO::getStatus, status)
                .count());
    }

    public Integer add(SlaveInfoPO slaveInfoPO) {
        return save(slaveInfoPO) ? 1 : 0;
    }

    public Integer update(SlaveInfoPO slaveInfoPO) {
        return updateById(slaveInfoPO) ? 1 : 0;
    }

    public Integer delete(String id) {
        return lambdaUpdate()
                .eq(SlaveInfoPO::getId, id)
                .eq(SlaveInfoPO::getStatus, "2")
                .remove() ? 1 : 0;
    }
}
