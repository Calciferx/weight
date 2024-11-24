package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.WeightRecordService;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/weightRecord")
public class WeightRecordController {
    @Autowired
    private WeightRecordService weightRecordService;

    @RequestMapping("query")
    public RespWrapper<List<WeightRecordDO>> getWeightInfo(@RequestParam("startTime") String startTimeStr, @RequestParam("endTime") String endTimeStr) {
        DateTime startTime = DateUtil.parse(startTimeStr);
        DateTime endTime = DateUtil.parse(endTimeStr);
        List<WeightRecordDO> list = weightRecordService.lambdaQuery()
                .between(WeightRecordDO::getWeighDate, startTime, endTime)
                .list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }

    @RequestMapping("saveOrUpdate")
    public RespWrapper<Boolean> saveOrUpdate(@RequestBody WeightRecordDO weightRecordDO) {
        boolean result = weightRecordService.saveOrUpdate(weightRecordDO);
        if (result) return new RespWrapper<>(RespCodeEnum.SUCCESS);
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    @RequestMapping("delete")
    public RespWrapper<Boolean> delete(@RequestBody WeightRecordDO weightRecordDO) {
        boolean result = weightRecordService.remove(new LambdaQueryWrapper<WeightRecordDO>().eq(WeightRecordDO::getId, weightRecordDO.getId()));
        if (result) return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "删除成功");
        return new RespWrapper<>(null, RespCodeEnum.ERROR, "删除失败");
    }
}
