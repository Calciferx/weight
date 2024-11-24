package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.MaterialDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @RequestMapping("queryAll")
    public RespWrapper<List<MaterialDO>> getSupplierInfoList() {
        List<MaterialDO> list = materialService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }
}
