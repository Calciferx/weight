package com.calcifer.weight.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecordVO {
    //    流水号
    @JsonProperty("流水号")
    private String serialNum;
    //    车号
    @JsonProperty("车号")
    private String carNum;
    //    过磅类型
    @JsonProperty("过磅类型")
    private String weighType;
    //    发货单位
    @JsonProperty("发货单位")
    private String goodSender;
    //    收货单位
    @JsonProperty("收货单位")
    private String goodReceiver;
    //    货名
    @JsonProperty("货名")
    private String goodName;
    //    规格
    @JsonProperty("规格")
    private String specification;
    //    毛重
    @JsonProperty("毛重")
    private Double roughWeight;
    //    皮重
    @JsonProperty("皮重")
    private Double tareWeight;
    //    净重
    @JsonProperty("净重")
    private Double netWeight;
    //    扣重
    @JsonProperty("扣重")
    private Double deductWeight;
    //    实重
    @JsonProperty("实重")
    private Double realWeight;
    //    单价
    @JsonProperty("单价")
    private Double unitPrice;
    //    金额
    @JsonProperty("金额")
    private Double amount;
    //    折方系数
    @JsonProperty("折方系数")
    private Double foldingCoefficient;
    //    方量
    @JsonProperty("方量")
    private Double cubeNum;
    //    过磅费
    @JsonProperty("过磅费")
    private Double weighFee;
    //    毛重司磅员
    @JsonProperty("毛重司磅员")
    private String roughWeighMan;
    //    皮重司磅员
    @JsonProperty("皮重司磅员")
    private String tareWeighMan;
    //    毛重磅号
    @JsonProperty("毛重磅号")
    private String roughWeighterId;
    //    皮重磅号
    @JsonProperty("皮重磅号")
    private String tareWeighterId;
    //    CONVERT(varchar,毛重时间,120) 毛重时间
    @JsonProperty("毛重时间")
    private String roughWeightTime;
    //    CONVERT(varchar,皮重时间,120) 皮重时间
    @JsonProperty("皮重时间")
    private String tareWeightTime;
    //    CONVERT(varchar,一次过磅时间,120) 一次过磅时间
    @JsonProperty("一次过磅时间")
    private String firstWeighTime;
    //    CONVERT(varchar,二次过磅时间,120) 二次过磅时间
    @JsonProperty("二次过磅时间")
    private String secondWeighTime;
    //    备用1,
    @JsonProperty("备用1")
    private String bak1;
    //    更新人
    @JsonProperty("更新人")
    private String updateBy;
    //    CONVERT(varchar,更新时间,120) 更新时间,
    @JsonProperty("更新时间")
    private String updateTime;
    //    备注
    @JsonProperty("备注")
    private String comment;
    //    客户类型
    @JsonProperty("客户类型")
    private String customerType;
    //    一次过磅重
    @JsonProperty("一次过磅重")
    private Double firstWeight;
    //    二次过磅重
    @JsonProperty("二次过磅重")
    private Double secondWeight;
    //    b0
    @JsonProperty("b0")
    private String b0;
    //    aguid
    @JsonProperty("aguid")
    private String aguid;
    //    PlanNumber
    @JsonProperty("PlanNumber")
    private String planNumber;
    //    RecordCreateMode
    @JsonProperty("RecordCreateMode")
    private String recordCreateMode;
    //    RecordFinish
    @JsonProperty("RecordFinish")
    private String recordFinish;
    //    LimitState
    @JsonProperty("LimitState")
    private String limitState;
    //            ManyID
    @JsonProperty("ManyID")
    private String manyId;
    //            多次净重
    @JsonProperty("多次净重")
    private String multiNetWeight;
}
