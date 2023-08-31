package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import lombok.Data;

@Data
public class RecordDto {
    //流水号
    private String serialNumber;
    //车号
    private String carNum;
    //过磅类型
    private String weightType;
    //发货单位
    private String shipper;
    //收货单位
    private String receivingUnit;
    //货名
    private String goodsName;
    //规格
    private String model;
    //毛重
    private String grossWeight;
    //皮重
    private String tareWeight;
    //净重
    private String netWeight;
    //扣重
    private String deductionWeight;
    //实重
    private String realWeight;
    //单价
    private String price;
    //金额
    private String money;
    //折方系数
    private String factor;
    //方量
    private String squareAmount;
    //过磅费
    private String weightFee;
    //毛重司磅员
    private String grossWeightman;
    //皮重司磅员
    private String tareWeightman;
    //毛重磅号
    private String grossHeavyWeight;
    //皮重磅号
    private String tareHeavyWeight;
    //CONVERT(varchar,毛重时间,120)
    private String grossWeightTime;
    //CONVERT(varchar,皮重时间,120)
    private String tareWeightTime;
    //CONVERT(varchar,一次过磅时间,120)
    private String firstWeightTime;
    //CONVERT(varchar,二次过磅时间,120)
    private String secondWeightTime;
    //备用1
    private String backup1;
    //更新人
    private String updateUser;
    //CONVERT(varchar,更新时间,120)
    private String updateTime;
    //备注
    private String remark;
    //客户类型
    private String clientType;
    //一次过磅重
    private String oneWeight;
    //二次过磅重
    private String twoWeight;
    private String b0;
    private String aguid;
    private String PlanNumber;
    private String RecordCreateMode;
    private String RecordFinish;
    private String LimitState;
    private String ManyID;
    //多次净重
    private String manyNetWeight;

    // 查询参数
    private String serialNo;
    private String carNo;
    private String weighingMode;
    private CompleteStatusEnum tareNull;
    private CompleteStatusEnum tareNull1;
    private String startTime;
    private String endTime;

    public RecordDto() {
    }

    public RecordDto(String carNum, String goodsName, String carNo, String startTime, String endTime, String weighingMode) {
        this.carNum = carNum;
        this.goodsName = goodsName;
        this.carNo = carNo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weighingMode = weighingMode;
    }
}
