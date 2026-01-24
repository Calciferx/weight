package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
@TableName("[称重信息]")
public class RecordPO {
    //    流水号
    @TableId("流水号")
    private String serialNum;
    //    车号
    @TableField("车号")
    private String carNo;
    //    过磅类型
    @TableField("过磅类型")
    private String weighType;
    //    发货单位
    @TableField("发货单位")
    private String goodsSender;
    //    收货单位
    @TableField("收货单位")
    private String goodsReceiver;
    //    货名
    @TableField("货名")
    private String goodsName;
    //    规格
    @TableField("规格")
    private String specification;
    //    毛重
    @TableField("毛重")
    private Double grossWeight;
    //    皮重
    @TableField("皮重")
    private Double tareWeight;
    //    净重
    @TableField("净重")
    private Double netWeight;
    //    扣重
    @TableField("扣重")
    private Double deductWeight;
    //    实重
    @TableField("实重")
    private Double realWeight;
    //    单价
    @TableField("单价")
    private Double unitPrice;
    //    金额
    @TableField("金额")
    private Double amount;
    //    折方系数
    @TableField("折方系数")
    private Double foldingCoefficient;
    //    方量
    @TableField("方量")
    private Double cubeNum;
    //    过磅费
    @TableField("过磅费")
    private Double weighFee;
    //    毛重司磅员
    @TableField("毛重司磅员")
    private String roughWeighMan;
    //    皮重司磅员
    @TableField("皮重司磅员")
    private String tareWeighMan;
    //    毛重磅号
    @TableField("毛重磅号")
    private String roughWeighterId;
    //    皮重磅号
    @TableField("皮重磅号")
    private String tareWeighterId;
    //    毛重时间
    @TableField("毛重时间")
    private Date roughWeightTime;
    //    皮重时间
    @TableField("皮重时间")
    private Date tareWeightTime;
    //    一次过磅时间
    @TableField("一次过磅时间")
    private Date firstWeighTime;
    //    二次过磅时间
    @TableField("二次过磅时间")
    private Date secondWeighTime;
    //    备用1,
    @TableField("备用1")
    private String bak1;
    // 备用13
    @TableField("备用13")
    private String bak13;
    // 备用14
    @TableField("备用14")
    private String bak14;
    //    更新人
    @TableField("更新人")
    private String updateBy;
    //    更新时间,
    @TableField("更新时间")
    private Date updateTime;
    //    备注
    @TableField("备注")
    private String comment;
    //    客户类型
    @TableField("客户类型")
    private String customerType;
    //    一次过磅重
    @TableField("一次过磅重")
    private Double firstWeight;
    //    二次过磅重
    @TableField("二次过磅重")
    private Double secondWeight;
    //    b0
    @TableField("b0")
    private String b0;
    //    aguid
    @TableField("aguid")
    private String aguid;
    //    PlanNumber
    @TableField("PlanNumber")
    private String planNumber;
    //    RecordCreateMode
    @TableField("RecordCreateMode")
    private String recordCreateMode;
    //    RecordFinish
    @TableField("RecordFinish")
    private String recordFinish;
    //    LimitState
    @TableField("LimitState")
    private String limitState;
    //            ManyID
    @TableField("ManyID")
    private String manyId;
    //            多次净重
    @TableField("多次净重")
    private String multiNetWeight;

    @TableField(exist = false)
    private String createTime;
    @TableField(exist = false)
    private Date startTime;
    @TableField(exist = false)
    private Date endTime;
    @TableField(exist = false)
    private CompleteStatusEnum tareNull1;
    @TableField(exist = false)
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

    public RecordPO(String carNo, CompleteStatusEnum tareNull) {
        this.carNo = carNo;
        this.tareNull = tareNull;
    }
}
