package com.calcifer.weight.repository;

import com.calcifer.weight.entity.dto.RecordDto;
import com.calcifer.weight.entity.domain.RecordPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordMapper {
    List<RecordPO> getRecordList(RecordPO recordPO);

    List<RecordDto> findRecordList(RecordDto recordDto);

    int updateRecord(RecordPO recordPO);

    int add(RecordPO recordPO);

    int deleteRecordByIds(List<String> ids);
}
