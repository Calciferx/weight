package com.calcifer.weight.service;

import com.calcifer.weight.entity.domain.LogInfo;
import com.calcifer.weight.repository.LogInfoMapper;
import com.calcifer.weight.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LogService {

    @Autowired
    private LogInfoMapper logInfoMapper;

    /**
     * 添加操作日志
     */
    public Boolean addLog(LogInfo logInfo) {
        logInfo.setType("系统操作");
        logInfo.setId(UUID.randomUUID().toString());
        logInfo.setOrderDate(DateUtil.getDay());
        logInfo.setCreateTime(DateUtil.getSdfTimes());
        return logInfoMapper.addSysLogInfo(logInfo) > 0;
    }

    /**
     * 查询操作日志
     *
     * @author lyb
     * @date 20201211
     */
    public List<LogInfo> findSysLogInfoByCondition(LogInfo logInfo) {
        return logInfoMapper.findSysLogInfoByCondition(logInfo);
    }
}
