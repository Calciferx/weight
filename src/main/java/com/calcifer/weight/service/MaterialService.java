package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.MaterialDO;
import com.calcifer.weight.repository.MaterialMapper;
import org.springframework.stereotype.Service;

@Service
public class MaterialService extends ServiceImpl<MaterialMapper, MaterialDO> {
}
