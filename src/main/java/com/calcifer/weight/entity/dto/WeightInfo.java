package com.calcifer.weight.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class WeightInfo {
    private String status;
    private int weightNum;
    private Date createTime;
}
