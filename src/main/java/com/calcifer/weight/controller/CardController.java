package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.entity.domain.CardDO;
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

    @RequestMapping("query")
    public RespWrapper<CardDO> queryCardDOByCardNum(String cardNum) {
        CardDO cardDO = cardInfoService.lambdaQuery().eq(CardDO::getCardNum, cardNum).one();
        return new RespWrapper<>(cardDO, RespCodeEnum.SUCCESS);
    }

    @RequestMapping("queryAll")
    public RespWrapper<List<CardDO>> queryAllCardDO() {
        List<CardDO> list = cardInfoService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }

    @RequestMapping("saveOrUpdate")
    public RespWrapper<Boolean> saveOrUpdate(@RequestBody CardDO CardDO) {
        boolean result = cardInfoService.saveOrUpdate(CardDO);
        if (result) return new RespWrapper<>(RespCodeEnum.SUCCESS);
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    @RequestMapping("delete")
    public RespWrapper<Boolean> delete(@RequestBody CardDO cardDO) {
        boolean result = cardInfoService.remove(new LambdaQueryWrapper<CardDO>().eq(CardDO::getId, cardDO.getId()));
        if (result) return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "删除成功");
        return new RespWrapper<>(null, RespCodeEnum.ERROR, "删除失败");
    }
}
