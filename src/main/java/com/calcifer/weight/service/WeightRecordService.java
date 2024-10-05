package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.repository.WeightRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class WeightRecordService extends ServiceImpl<WeightRecordMapper, WeightRecordDO> implements IService<WeightRecordDO> {

}
