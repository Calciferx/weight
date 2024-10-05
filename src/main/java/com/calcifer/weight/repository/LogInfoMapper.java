package com.calcifer.weight.repository;

import com.calcifer.weight.entity.domain.LogInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogInfoMapper {
    Integer addSysLogInfo(LogInfo logInfo);

    List<LogInfo> findSysLogInfoByCondition(LogInfo logInfo);
}
