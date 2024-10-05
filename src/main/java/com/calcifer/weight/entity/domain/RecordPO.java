package com.calcifer.weight.entity.domain;

import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class RecordPO {
    //    流水号
    private String serialNum;
    //    车号
    private String carNum;
    //    过磅类型
    private String weighType;
    //    发货单位
    private String goodSender;
    //    收货单位
    private String goodReceiver;
    //    货名
    private String goodName;
    //    规格
    private String specification;
    //    毛重
    private Double roughWeight;
    //    皮重
    private Double tareWeight;
    //    净重
    private Double netWeight;
    //    扣重
    private Double deductWeight;
    //    实重
    private Double realWeight;
    //    单价
    private Double unitPrice;
    //    金额
    private Double amount;
    //    折方系数
    private Double foldingCoefficient;
    //    方量
    private Double cubeNum;
    //    过磅费
    private Double weighFee;
    //    毛重司磅员
    private String roughWeighMan;
    //    皮重司磅员
    private String tareWeighMan;
    //    毛重磅号
    private String roughWeighterId;
    //    皮重磅号
    private String tareWeighterId;
    //    CONVERT(varchar,毛重时间,120) 毛重时间
    private String roughWeightTime;
    //    CONVERT(varchar,皮重时间,120) 皮重时间
    private String tareWeightTime;
    //    CONVERT(varchar,一次过磅时间,120) 一次过磅时间
    private String firstWeighTime;
    //    CONVERT(varchar,二次过磅时间,120) 二次过磅时间
    private String secondWeighTime;
    //    备用1,
    private String bak1;
    // 备用13
    private String bak13;
    // 备用14
    private String bak14;
    //    更新人
    private String updateBy;
    //    CONVERT(varchar,更新时间,120) 更新时间,
    private String updateTime;
    //    备注
    private String comment;
    //    客户类型
    private String customerType;
    //    一次过磅重
    private Double firstWeight;
    //    二次过磅重
    private Double secondWeight;
    //    b0
    private String b0;
    //    aguid
    private String aguid;
    //    PlanNumber
    private String planNumber;
    //    RecordCreateMode
    private String recordCreateMode;
    //    RecordFinish
    private String recordFinish;
    //    LimitState
    private String limitState;
    //            ManyID
    private String manyId;
    //            多次净重
    private String multiNetWeight;

    private String createTime;
    private Date startTime;
    private Date endTime;
    private CompleteStatusEnum tareNull1;
    private CompleteStatusEnum tareNull;

    public RecordPO() {
    }

    public RecordPO(String createTime, Date startTime, Date endTime, CompleteStatusEnum tareNull1, CompleteStatusEnum tareNull) {
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tareNull1 = tareNull1;
        this.tareNull = tareNull;
    }

    public RecordPO(String carNum, CompleteStatusEnum tareNull) {
        this.carNum = carNum;
        this.tareNull = tareNull;
    }
}
