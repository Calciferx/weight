package com.calcifer.weight.service;

import com.calcifer.weight.entity.dto.RecordDto;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.calcifer.weight.entity.domain.RecordPO;
import com.calcifer.weight.entity.vo.RecordVO;
import com.calcifer.weight.repository.RecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {
    @Autowired
    private RecordMapper recordMapper;

    public List<RecordVO> getRecordList(String createTime, Date startTime, Date endTime, CompleteStatusEnum tareNull1, CompleteStatusEnum tareNull) {
        RecordPO recordPO = new RecordPO(createTime, startTime, endTime, tareNull1, tareNull);
        List<RecordPO> recordList = recordMapper.getRecordList(recordPO);
        return recordList.stream().map(RetRecordPO -> {
            RecordVO recordVO = new RecordVO();
            BeanUtils.copyProperties(RetRecordPO, recordVO);
            return recordVO;
        }).collect(Collectors.toList());
    }

    public List<RecordVO> getRecordList(RecordPO recordPO) {
        List<RecordPO> recordList = recordMapper.getRecordList(recordPO);
        return recordList.stream().map(RetRecordPO -> {
            RecordVO recordVO = new RecordVO();
            BeanUtils.copyProperties(RetRecordPO, recordVO);
            return recordVO;
        }).collect(Collectors.toList());
    }

    public List<RecordPO> getRecordList(String carNum, CompleteStatusEnum tareNull) {
        RecordPO recordPO = new RecordPO(carNum, tareNull);
        return recordMapper.getRecordList(recordPO);
    }

    public List<RecordDto> findRecordList(RecordDto recordDto) {
        return recordMapper.findRecordList(recordDto);
    }

    public int updateRecord(RecordPO recordPO) {
        return recordMapper.updateRecord(recordPO);
    }

    public int addRecord(RecordPO recordPO) {
        return recordMapper.add(recordPO);
    }

    public int deleteRecordByIds(String[] ids) {
        List<String> idsList = Arrays.asList(ids);
        return recordMapper.deleteRecordByIds(idsList);
    }
}
