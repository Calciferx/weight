package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.CarDO;
import com.calcifer.weight.repository.CarMapper;
import org.springframework.stereotype.Service;

@Service
public class CarService extends ServiceImpl<CarMapper, CarDO> {
}
