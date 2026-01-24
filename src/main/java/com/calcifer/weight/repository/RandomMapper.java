package com.calcifer.weight.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calcifer.weight.entity.po.WgRandom;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomMapper extends BaseMapper<WgRandom> {
}
