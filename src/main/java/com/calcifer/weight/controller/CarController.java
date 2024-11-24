package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.CarDO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/car")
public class CarController {
    @Autowired
    private CarService carService;

    @RequestMapping("queryAll")
    public RespWrapper<List<CarDO>> getSupplierInfoList() {
        List<CarDO> list = carService.list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }
}
