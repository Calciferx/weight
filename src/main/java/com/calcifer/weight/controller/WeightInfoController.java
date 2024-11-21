package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.WeightInfoDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.WeightInfoService;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/weightInfo")
public class WeightInfoController {
    @Autowired
    private WeightInfoService weightInfoService;

    @RequestMapping("query")
    public RespWrapper<List<WeightInfoDO>> getWeightInfo(@RequestParam("startTime") String startTimeStr, @RequestParam("endTime") String endTimeStr) {
        DateTime startTime = DateUtil.parse(startTimeStr);
        DateTime endTime = DateUtil.parse(endTimeStr);
        List<WeightInfoDO> list = weightInfoService.lambdaQuery()
                .between(WeightInfoDO::getWeighDate, startTime, endTime)
                .list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }
}
