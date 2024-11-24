package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.SupplierDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.SupplierInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/supplierInfo")
public class SupplierInfoController {
    @Autowired
    private SupplierInfoService supplierInfoService;

    @RequestMapping("queryAll")
    public RespWrapper<List<SupplierDO>> getSupplierInfoList() {
        List<SupplierDO> list = supplierInfoService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }
}
