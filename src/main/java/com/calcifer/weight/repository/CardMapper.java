package com.calcifer.weight.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calcifer.weight.entity.po.CardInfoPO;
import org.springframework.stereotype.Repository;

@Repository
public interface CardMapper extends BaseMapper<CardInfoPO> {
}
