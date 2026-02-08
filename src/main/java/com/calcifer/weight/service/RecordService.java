package com.calcifer.weight.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.dto.RecordDTO;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.calcifer.weight.entity.po.RecordPO;
import com.calcifer.weight.entity.vo.RecordVO;
import com.calcifer.weight.repository.RecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService extends ServiceImpl<RecordMapper, RecordPO> {

    public List<RecordVO> getRecordList(String createTime, Date startTime, Date endTime, CompleteStatusEnum tareNull1, CompleteStatusEnum tareNull) {
        RecordPO recordPO = new RecordPO(createTime, startTime, endTime, tareNull1, tareNull);
        return this.getRecordList(recordPO);
    }

    public List<RecordVO> getRecordList(RecordPO recordPO) {
        LambdaQueryWrapper<RecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(recordPO.getCarNo()), RecordPO::getCarNo, recordPO.getCarNo());
        
        if (recordPO.getTareNull() != null) {
            wrapper.eq(RecordPO::getBak1, recordPO.getTareNull());
        }
        if (recordPO.getTareNull1() != null) {
            wrapper.and(w -> w.isNull(RecordPO::getBak1).or().ne(RecordPO::getBak1, "1"));
        }
        
        boolean hasTimeCondition = false;
        if (StringUtils.isNotBlank(recordPO.getCreateTime())) {
            hasTimeCondition = true;
            String timeLike = "%" + recordPO.getCreateTime() + "%";
            wrapper.and(w -> w.apply("CONVERT(varchar, 一次过磅时间, 120) LIKE {0}", timeLike)
                    .or().apply("CONVERT(varchar, 二次过磅时间, 120) LIKE {0}", timeLike));
        }
        
        if (recordPO.getStartTime() != null) {
            hasTimeCondition = true;
            wrapper.and(w -> w.ge(RecordPO::getFirstWeighTime, recordPO.getStartTime())
                    .or().ge(RecordPO::getSecondWeighTime, recordPO.getStartTime()));
        }
        if (recordPO.getEndTime() != null) {
            hasTimeCondition = true;
            wrapper.and(w -> w.le(RecordPO::getFirstWeighTime, recordPO.getEndTime())
                    .or().le(RecordPO::getSecondWeighTime, recordPO.getEndTime()));
        }

        // Default condition if no specific filters
        if (!hasTimeCondition && recordPO.getTareNull() == null && recordPO.getTareNull1() == null && StringUtils.isBlank(recordPO.getCarNo())) {
             String today = DateUtil.formatDate(new Date());
             String timeLike = "%" + today + "%";
             wrapper.and(w -> w.apply("CONVERT(varchar, 一次过磅时间, 23) LIKE {0}", timeLike)
                     .or().apply("CONVERT(varchar, 二次过磅时间, 23) LIKE {0}", timeLike));
        }

        List<RecordPO> recordList = baseMapper.selectList(wrapper);
        return recordList.stream().map(po -> {
            RecordVO recordVO = new RecordVO();
            BeanUtils.copyProperties(po, recordVO);
            return recordVO;
        }).collect(Collectors.toList());
    }

    public List<RecordPO> getRecordList(String carNum, CompleteStatusEnum tareNull) {
        LambdaQueryWrapper<RecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(carNum), RecordPO::getCarNo, carNum);
        if (tareNull != null) {
             wrapper.eq(RecordPO::getBak1, tareNull);
        }
        return baseMapper.selectList(wrapper);
    }

    public List<RecordDTO> findRecordList(RecordDTO dto) {
        LambdaQueryWrapper<RecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(dto.getSerialNo()), RecordPO::getSerialNum, dto.getSerialNo());
        wrapper.like(StringUtils.isNotBlank(dto.getCarNum()), RecordPO::getCarNo, dto.getCarNum());
        wrapper.eq(StringUtils.isNotBlank(dto.getCarNo()), RecordPO::getCarNo, dto.getCarNo());
        wrapper.eq(StringUtils.isNotBlank(dto.getWeighingMode()), RecordPO::getBak13, dto.getWeighingMode());
        
        if (dto.getTareNull() != null) {
            wrapper.eq(RecordPO::getBak1, dto.getTareNull());
        }
        if (dto.getTareNull1() != null) {
            wrapper.and(w -> w.ne(RecordPO::getBak1, "1").or().isNull(RecordPO::getBak1));
        }
        
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            wrapper.ge(RecordPO::getUpdateTime, dto.getStartTime()); // Using UpdateTime as per XML
        }
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            wrapper.le(RecordPO::getUpdateTime, dto.getEndTime());
        }

        List<RecordPO> poList = baseMapper.selectList(wrapper);
        return poList.stream().map(po -> {
            RecordDTO outDto = new RecordDTO();
            outDto.setSerialNumber(po.getSerialNum());
            outDto.setCarNum(po.getCarNo());
            outDto.setWeightType(po.getWeighType());
            outDto.setShipper(po.getGoodsSender());
            outDto.setReceivingUnit(po.getGoodsReceiver());
            outDto.setGoodsName(po.getGoodsName());
            outDto.setModel(po.getSpecification());
            outDto.setGrossWeight(po.getGrossWeight() != null ? String.valueOf(po.getGrossWeight()) : null);
            outDto.setTareWeight(po.getTareWeight() != null ? String.valueOf(po.getTareWeight()) : null);
            outDto.setNetWeight(po.getNetWeight() != null ? String.valueOf(po.getNetWeight()) : null);
            outDto.setDeductionWeight(po.getDeductWeight() != null ? String.valueOf(po.getDeductWeight()) : null);
            outDto.setRealWeight(po.getRealWeight() != null ? String.valueOf(po.getRealWeight()) : null);
            outDto.setPrice(po.getUnitPrice() != null ? String.valueOf(po.getUnitPrice()) : null);
            outDto.setMoney(po.getAmount() != null ? String.valueOf(po.getAmount()) : null);
            outDto.setFactor(po.getFoldingCoefficient() != null ? String.valueOf(po.getFoldingCoefficient()) : null);
            outDto.setSquareAmount(po.getCubeNum() != null ? String.valueOf(po.getCubeNum()) : null);
            outDto.setWeightFee(po.getWeighFee() != null ? String.valueOf(po.getWeighFee()) : null);
            outDto.setGrossWeightman(po.getRoughWeighMan());
            outDto.setTareWeightman(po.getTareWeighMan());
            outDto.setGrossHeavyWeight(po.getRoughWeighterId());
            outDto.setTareHeavyWeight(po.getTareWeighterId());
            outDto.setGrossWeightTime(DateUtil.format(po.getRoughWeightTime(), "yyyy-MM-dd HH:mm:ss"));
            outDto.setTareWeightTime(DateUtil.format(po.getTareWeightTime(), "yyyy-MM-dd HH:mm:ss"));
            outDto.setFirstWeightTime(DateUtil.format(po.getFirstWeighTime(), "yyyy-MM-dd HH:mm:ss"));
            outDto.setSecondWeightTime(DateUtil.format(po.getSecondWeighTime(), "yyyy-MM-dd HH:mm:ss"));
            outDto.setBackup1(po.getBak1());
            outDto.setUpdateUser(po.getUpdateBy());
            outDto.setUpdateTime(DateUtil.format(po.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            outDto.setRemark(po.getComment());
//            outDto.setClientType(po.getCustomerType());
            outDto.setOneWeight(po.getFirstWeight() != null ? String.valueOf(po.getFirstWeight()) : null);
            outDto.setTwoWeight(po.getSecondWeight() != null ? String.valueOf(po.getSecondWeight()) : null);
            outDto.setB0(po.getB0());
            outDto.setAguid(po.getAguid());
            outDto.setPlanNumber(po.getPlanNumber());
//            outDto.setRecordCreateMode(po.getRecordCreateMode());
//            outDto.setRecordFinish(po.getRecordFinish());
//            outDto.setLimitState(po.getLimitState());
            outDto.setManyID(po.getManyId());
//            outDto.setManyNetWeight(po.getMultiNetWeight());
            return outDto;
        }).collect(Collectors.toList());
    }

    public int updateRecord(RecordPO recordPO) {
        recordPO.setUpdateTime(new Date());
        recordPO.setRecordFinish(1);
        return updateById(recordPO) ? 1 : 0;
    }

    public int addRecord(RecordPO recordPO) {
        recordPO.setUpdateTime(new Date());
        return save(recordPO) ? 1 : 0;
    }

    public int deleteRecordByIds(String[] ids) {
        List<String> idsList = Arrays.asList(ids);
        return removeByIds(idsList) ? 1 : 0;
    }
}
