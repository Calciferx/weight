package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.CardInfoPO;
import com.calcifer.weight.repository.CardMapper;
import org.springframework.stereotype.Service;

@Service
public class CardService extends ServiceImpl<CardMapper, CardInfoPO> {
}
