package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.LogInfo;
import com.calcifer.weight.repository.LogInfoMapper;
import com.calcifer.weight.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LogService extends ServiceImpl<LogInfoMapper, LogInfo> {

    /**
     * 添加操作日志
     */
    public Boolean addLog(LogInfo logInfo) {
        logInfo.setType("系统操作");
        logInfo.setId(UUID.randomUUID().toString());
        logInfo.setOrderDate(DateUtil.getDay());
        logInfo.setCreateTime(DateUtil.getSdfTimes());
        return save(logInfo);
    }

    /**
     * 查询操作日志
     *
     * @author lyb
     * @date 20201211
     */
    public List<LogInfo> findSysLogInfoByCondition(LogInfo logInfo) {
        return lambdaQuery()
                .like(StringUtils.isNotBlank(logInfo.getType()), LogInfo::getType, logInfo.getType())
                .like(StringUtils.isNotBlank(logInfo.getModular()), LogInfo::getModular, logInfo.getModular())
                .like(StringUtils.isNotBlank(logInfo.getFunctionLog()), LogInfo::getFunctionLog, logInfo.getFunctionLog())
                .like(StringUtils.isNotBlank(logInfo.getOperationLog()), LogInfo::getOperationLog, logInfo.getOperationLog())
                .like(StringUtils.isNotBlank(logInfo.getNameLog()), LogInfo::getNameLog, logInfo.getNameLog())
                .like(StringUtils.isNotBlank(logInfo.getIpLog()), LogInfo::getIpLog, logInfo.getIpLog())
                .like(StringUtils.isNotBlank(logInfo.getOrderDate()), LogInfo::getOrderDate, logInfo.getOrderDate())
                .like(StringUtils.isNotBlank(logInfo.getCode()), LogInfo::getCode, logInfo.getCode())
                .like(StringUtils.isNotBlank(logInfo.getContent()), LogInfo::getContent, logInfo.getContent())
                .like(StringUtils.isNotBlank(logInfo.getLogType()), LogInfo::getLogType, logInfo.getLogType())
                .like(StringUtils.isNotBlank(logInfo.getBusinessId()), LogInfo::getBusinessId, logInfo.getBusinessId())
                .orderByDesc(LogInfo::getCreateTime)
                .list();
    }
}
