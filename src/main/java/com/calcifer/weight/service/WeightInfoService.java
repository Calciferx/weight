package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.WeightInfoDO;
import com.calcifer.weight.repository.WeightInfoMapper;
import org.springframework.stereotype.Service;

@Service
public class WeightInfoService extends ServiceImpl<WeightInfoMapper, WeightInfoDO> {
}
