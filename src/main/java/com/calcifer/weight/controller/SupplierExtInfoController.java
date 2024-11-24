package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.entity.domain.SupplierExtInfoDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.SupplierExtInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/supplierExtInfo")
public class SupplierExtInfoController {
    @Autowired
    private SupplierExtInfoService supplierExtInfoService;

    @RequestMapping("queryAll")
    public RespWrapper<List<SupplierExtInfoDO>> getWeightInfo() {
        List<SupplierExtInfoDO> list = supplierExtInfoService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }

    @RequestMapping("saveOrUpdate")
    public RespWrapper<Boolean> saveOrUpdate(@RequestBody SupplierExtInfoDO supplierExtInfoDO) {
        boolean result = supplierExtInfoService.saveOrUpdate(supplierExtInfoDO);
        if (result) return new RespWrapper<>(RespCodeEnum.SUCCESS);
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    @RequestMapping("delete")
    public RespWrapper<Boolean> delete(@RequestBody SupplierExtInfoDO supplierExtInfoDO) {
        boolean result = supplierExtInfoService.remove(new LambdaQueryWrapper<SupplierExtInfoDO>().eq(SupplierExtInfoDO::getId, supplierExtInfoDO.getId()));
        if (result) return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "删除成功");
        return new RespWrapper<>(null, RespCodeEnum.ERROR, "删除失败");
    }
}
