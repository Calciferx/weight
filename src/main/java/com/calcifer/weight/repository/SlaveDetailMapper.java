package com.calcifer.weight.repository;

import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlaveDetailMapper {
    List<SlaveDetailInfo> querySlaveDetailInfo(SlaveDetailInfo slaveDetailInfo);

    Integer addDetails(SlaveDetailInfo slaveDetailInfo);

    Integer delete(SlaveDetailInfo slaveDetailInfo);
}
