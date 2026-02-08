package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.Date;

@Data
@TableName("[称重信息]")
public class RecordPO {
    @TableId(value = "序号", type = IdType.AUTO)
    @JsonAlias("序号")
    private Integer id;

    @TableField("流水号")
    @JsonAlias("流水号")
    private String serialNum;

    @TableField("车号")
    @JsonAlias("车号")
    private String carNo;

    @TableField("过磅类型")
    @JsonAlias("过磅类型")
    private String weighType;

    @TableField("发货单位")
    @JsonAlias("发货单位")
    private String goodsSender;

    @TableField("收货单位")
    @JsonAlias("收货单位")
    private String goodsReceiver;

    @TableField("货名")
    @JsonAlias("货名")
    private String goodsName;

    @TableField("规格")
    @JsonAlias("规格")
    private String specification;

    @TableField("毛重")
    @JsonAlias("毛重")
    private Double grossWeight;

    @TableField("皮重")
    @JsonAlias("皮重")
    private Double tareWeight;

    @TableField("净重")
    @JsonAlias("净重")
    private Double netWeight;

    @TableField("扣重")
    @JsonAlias("扣重")
    private Double deductWeight;

    @TableField("实重")
    @JsonAlias("实重")
    private Double realWeight;

    @TableField("单价")
    @JsonAlias("单价")
    private Double unitPrice;

    @TableField("金额")
    @JsonAlias("金额")
    private Double amount;

    @TableField("折方系数")
    @JsonAlias("折方系数")
    private Double foldingCoefficient;

    @TableField("方量")
    @JsonAlias("方量")
    private Double cubeNum;

    @TableField("过磅费")
    @JsonAlias("过磅费")
    private Double weighFee;

    @TableField("毛重司磅员")
    @JsonAlias("毛重司磅员")
    private String roughWeighMan;

    @TableField("皮重司磅员")
    @JsonAlias("皮重司磅员")
    private String tareWeighMan;

    @TableField("毛重磅号")
    @JsonAlias("毛重磅号")
    private String roughWeighterId;

    @TableField("皮重磅号")
    @JsonAlias("皮重磅号")
    private String tareWeighterId;

    @TableField("毛重时间")
    @JsonAlias("毛重时间")
    private Date roughWeightTime;

    @TableField("皮重时间")
    @JsonAlias("皮重时间")
    private Date tareWeightTime;

    @TableField("一次过磅时间")
    @JsonAlias("一次过磅时间")
    private Date firstWeighTime;

    @TableField("二次过磅时间")
    @JsonAlias("二次过磅时间")
    private Date secondWeighTime;

    @TableField("更新人")
    @JsonAlias("更新人")
    private String updateBy;

    @TableField("更新时间")
    @JsonAlias("更新时间")
    private Date updateTime;

    @TableField("备注")
    @JsonAlias("备注")
    private String comment;

    @TableField("打印次数")
    @JsonAlias("打印次数")
    private Integer printCount;

    @TableField("上传否")
    @JsonAlias("上传否")
    private Boolean uploaded;

    @TableField("备用1")
    @JsonAlias("备用1")
    private String bak1;

    @TableField("备用2")
    @JsonAlias("备用2")
    private String bak2;

    @TableField("备用3")
    @JsonAlias("备用3")
    private String bak3;

    @TableField("备用4")
    @JsonAlias("备用4")
    private String bak4;

    @TableField("备用5")
    @JsonAlias("备用5")
    private String bak5;

    @TableField("备用6")
    @JsonAlias("备用6")
    private Double bak6;

    @TableField("备用7")
    @JsonAlias("备用7")
    private Double bak7;

    @TableField("备用8")
    @JsonAlias("备用8")
    private Double bak8;

    @TableField("备用9")
    @JsonAlias("备用9")
    private Double bak9;

    @TableField("备用10")
    @JsonAlias("备用10")
    private String bak10;

    @TableField("备用11")
    @JsonAlias("备用11")
    private String bak11;

    @TableField("备用12")
    @JsonAlias("备用12")
    private String bak12;

    @TableField("备用13")
    @JsonAlias("备用13")
    private String bak13;

    @TableField("备用14")
    @JsonAlias("备用14")
    private String bak14;

    @TableField("备用15")
    @JsonAlias("备用15")
    private Double bak15;

    @TableField("备用16")
    @JsonAlias("备用16")
    private Double bak16;

    @TableField("备用17")
    @JsonAlias("备用17")
    private Double bak17;

    @TableField("备用18")
    @JsonAlias("备用18")
    private Double bak18;

    @TableField("b0")
    @JsonAlias("b0")
    private String b0;

    @TableField("aguid")
    @JsonAlias("aguid")
    private String aguid;

    @TableField("客户类型")
    @JsonAlias("客户类型")
    private Integer customerType;

    @TableField("e_upimg")
    @JsonAlias("e_upimg")
    private String eUpImg;

    @TableField("RecordCreateMode")
    @JsonAlias("RecordCreateMode")
    private Integer recordCreateMode;

    @TableField("备用19")
    @JsonAlias("备用19")
    private String bak19;

    @TableField("备用20")
    @JsonAlias("备用20")
    private String bak20;

    @TableField("备用21")
    @JsonAlias("备用21")
    private String bak21;

    @TableField("备用22")
    @JsonAlias("备用22")
    private String bak22;

    @TableField("备用23")
    @JsonAlias("备用23")
    private String bak23;

    @TableField("备用24")
    @JsonAlias("备用24")
    private String bak24;

    @TableField("备用25")
    @JsonAlias("备用25")
    private String bak25;

    @TableField("备用26")
    @JsonAlias("备用26")
    private String bak26;

    @TableField("备用27")
    @JsonAlias("备用27")
    private String bak27;

    @TableField("备用28")
    @JsonAlias("备用28")
    private String bak28;

    @TableField("driver_info")
    @JsonAlias("driver_info")
    private String driverInfo;

    @TableField("modify_onnet")
    @JsonAlias("modify_onnet")
    private String modifyOnnet;

    @TableField("modify_time")
    @JsonAlias("modify_time")
    private Date modifyTime;

    @TableField("modify_by")
    @JsonAlias("modify_by")
    private String modifyBy;

    @TableField("audit_flag")
    @JsonAlias("audit_flag")
    private String auditFlag;

    @TableField("audit_time")
    @JsonAlias("audit_time")
    private Date auditTime;

    @TableField("audit_by")
    @JsonAlias("audit_by")
    private String auditBy;

    @TableField("一次过磅重")
    @JsonAlias("一次过磅重")
    private Double firstWeight;

    @TableField("二次过磅重")
    @JsonAlias("二次过磅重")
    private Double secondWeight;

    @TableField("PlanNumber")
    @JsonAlias("PlanNumber")
    private String planNumber;

    @TableField("RecordFinish")
    @JsonAlias("RecordFinish")
    private Integer recordFinish;

    @TableField("网价同步时间")
    @JsonAlias("网价同步时间")
    private Date netPriceSyncTime;

    @TableField("网价修改人")
    @JsonAlias("网价修改人")
    private String netPriceModifier;

    @TableField("LimitState")
    @JsonAlias("LimitState")
    private Integer limitState;

    @TableField("ManyID")
    @JsonAlias("ManyID")
    private String manyId;

    @TableField("多次净重")
    @JsonAlias("多次净重")
    private Double multiNetWeight;

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

}
