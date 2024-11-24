package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.CardDO;
import com.calcifer.weight.repository.CardInfoMapper;
import org.springframework.stereotype.Service;

@Service
public class CardInfoService extends ServiceImpl<CardInfoMapper, CardDO> {
}
