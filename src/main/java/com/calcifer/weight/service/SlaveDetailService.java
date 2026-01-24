package com.calcifer.weight.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.po.SlaveDetail;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.repository.SlaveDetailMapper;
import com.calcifer.weight.repository.SlaveMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SlaveDetailService extends ServiceImpl<SlaveDetailMapper, SlaveDetail> {

    @Autowired
    private SlaveMapper slaveMapper;

    public List<SlaveDetailInfo> querySlaveDetailInfo(String slaveId, String status, String slaveIp) {
        String targetSlaveId = slaveId;
        
        if (StringUtils.isNotBlank(slaveIp)) {
            SlaveInfo slave = slaveMapper.selectOne(new LambdaQueryWrapper<SlaveInfo>().eq(SlaveInfo::getSlaveIp, slaveIp));
            if (slave == null) {
                return new ArrayList<>();
            }
            if (StringUtils.isNotBlank(targetSlaveId) && !targetSlaveId.equals(slave.getId())) {
                 return new ArrayList<>(); // IP matches different ID than requested
            }
            targetSlaveId = slave.getId();
        }

        LambdaQueryWrapper<SlaveDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(targetSlaveId), SlaveDetail::getSlaveId, targetSlaveId);
        wrapper.eq(StringUtils.isNotBlank(status), SlaveDetail::getStatus, status);
        wrapper.orderByAsc(SlaveDetail::getSerialName, SlaveDetail::getSerialSort);
        
        List<SlaveDetail> details = list(wrapper);
        
        if (details.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> slaveIds = details.stream().map(SlaveDetail::getSlaveId).collect(Collectors.toSet());
        Map<String, SlaveInfo> slaveMap = slaveMapper.selectBatchIds(slaveIds).stream()
                .collect(Collectors.toMap(SlaveInfo::getId, s -> s));

        return details.stream().map(d -> {
            SlaveDetailInfo info = new SlaveDetailInfo();
            BeanUtils.copyProperties(d, info);
            SlaveInfo s = slaveMap.get(d.getSlaveId());
            if (s != null) {
                info.setSlaveIp(s.getSlaveIp());
                info.setCoilNum(String.valueOf(s.getCoilNum()));
                info.setDiscreteNum(String.valueOf(s.getDiscreteNum()));
            }
            return info;
        }).collect(Collectors.toList());
    }

    public List<SlaveDetailInfo> querySlaveDetailInfoBySlaveId(String slaveId) {
        return querySlaveDetailInfo(slaveId, "1", null);
    }

    public Integer addDetails(SlaveDetailInfo slaveDetailInfo) {
        SlaveDetail po = new SlaveDetail();
        BeanUtils.copyProperties(slaveDetailInfo, po);
        return save(po) ? 1 : 0;
    }

    public Integer delete(String slaveId) {
        return remove(new LambdaQueryWrapper<SlaveDetail>().eq(SlaveDetail::getSlaveId, slaveId)) ? 1 : 0;
    }
}
