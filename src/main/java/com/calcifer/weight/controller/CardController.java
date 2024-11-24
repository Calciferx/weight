package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.entity.domain.CardInfoDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/cardInfo")
public class CardController {
    @Autowired
    private CardInfoService cardInfoService;

    @RequestMapping("queryAll")
    public RespWrapper<List<CardInfoDO>> getWeightInfo() {
        List<CardInfoDO> list = cardInfoService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }

    @RequestMapping("saveOrUpdate")
    public RespWrapper<Boolean> saveOrUpdate(@RequestBody CardInfoDO CardInfoDO) {
        boolean result = cardInfoService.saveOrUpdate(CardInfoDO);
        if (result) return new RespWrapper<>(RespCodeEnum.SUCCESS);
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    @RequestMapping("delete")
    public RespWrapper<Boolean> delete(@RequestBody CardInfoDO cardInfoDO) {
        boolean result = cardInfoService.remove(new LambdaQueryWrapper<CardInfoDO>().eq(CardInfoDO::getId, cardInfoDO.getId()));
        if (result) return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "删除成功");
        return new RespWrapper<>(null, RespCodeEnum.ERROR, "删除失败");
    }
}
