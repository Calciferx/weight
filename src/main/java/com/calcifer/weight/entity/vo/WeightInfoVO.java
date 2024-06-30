package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.dto.WeightInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeightInfoVO {
    //TODO 变量名规范，需同步前端修改
    private int dataHex;
    private String type;
    private WeightInfo map;
}
