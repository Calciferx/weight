package com.calcifer.weight.repository;

import com.calcifer.weight.entity.po.SlaveInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlaveMapper {
    Integer add(SlaveInfo slaveInfo);

    List<SlaveInfo> querySlaveInfo(SlaveInfo slaveInfo);

    Integer count(SlaveInfo slaveInfo);

    Integer update(SlaveInfo slaveInfo);

    Integer delete(SlaveInfo slaveInfo);
}
