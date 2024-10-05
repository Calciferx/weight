package com.calcifer.weight.entity.domain;

import lombok.Data;

@Data
public class Record {
    //序号
    private Integer identity;
    //流水号
    private String serialNum;
    //车号
    private String carNum;
    //过磅类型
    private String weighType;
    //发货单位
    private String goodSender;
    //收货单位
    private String goodReceiver;
    //货名
    private String goodName;
    //规格
    private String specification;
    //毛重
    private Double roughWeight;
    //皮重
    private Double tareWeight;
    //净重
    private Double netWeight;
    //扣重
    private Double deductWeight;
    //实重
    private Double realWeight;
    //单价
    private Double unitPrice;
    //金额
    private Double amount;
    //折方系数
    private Double foldingCoefficient;
    //方量
    private Double cubeNum;
    //过磅费
    private Double weighFee;
    //毛重司磅员
    private String roughWeighMan;
    //皮重司磅员
    private String tareWeighMan;
    //毛重磅号
    private String roughWeighterId;
    //皮重磅号
    private String tareWeighterId;
    //    CONVERT(varchar,毛重时间,120) 毛重时间
    private String roughWeightTime;
    //    CONVERT(varchar,皮重时间,120) 皮重时间
    private String tareWeightTime;
    //    CONVERT(varchar,一次过磅时间,120) 一次过磅时间
    private String firstWeighTime;
    //    CONVERT(varchar,二次过磅时间,120) 二次过磅时间
    private String secondWeighTime;
    //    更新人
    private String updateBy;
    //    CONVERT(varchar,更新时间,120) 更新时间,
    private String updateTime;
    //    备注
    private String comment;
    //打印次数
    private int printCount;
    //上传否
    private Byte isUpload;
    //备用1
    private String bak1;
    //备用2
    private String bak2;
    //备用3
    private String bak3;
    //备用4
    private String bak4;
    //备用5
    private String bak5;
    //备用6
    private Double bak6;
    //备用7
    private Double bak7;
    //备用8
    private Double bak8;
    //备用9
    private Double bak9;
    //备用10
    private String bak10;
    //备用11
    private String bak11;
    //备用12
    private String bak12;
    //备用13
    private String bak13;
    //备用14
    private String bak14;
    //备用15
    private Double bak15;
    //备用16
    private Double bak16;
    //备用17
    private Double bak17;
    //备用18
    private Double bak18;
    //    b0
    private String b0;
    //    aguid
    private String aguid;
    //    客户类型
    private Integer customerType;
    //e_upimg
    private String eUpimg;
    //    RecordCreateMode
    private Integer recordCreateMode;
    //备用19
    private String bak19;
    //备用20
    private String bak20;
    //备用21
    private String bak21;
    //备用22
    private String bak22;
    //备用23
    private String bak23;
    //备用24
    private String bak24;
    //备用25
    private String bak25;
    //备用26
    private String bak26;
    //备用27
    private String bak27;
    //备用28
    private String bak28;
    //driver_info
    private String driverInfo;
    //modify_onnet
    private String modifyOnnet;
    //modify_time datetime(23, 3)
    private String modifyTime;
    //modify_by
    private String modifyBy;
    //audit_flag
    private String auditFlag;
    //audit_time datetime(23, 3)
    private String auditTime;
    //audit_by
    private String auditBy;
    //    一次过磅重
    private Double firstWeight;
    //    二次过磅重
    private Double secondWeight;
    //    PlanNumber
    private String planNumber;
    //    RecordFinish
    private Integer recordFinish;
    //网价同步时间 datetime(23, 3)
    private String netPriceSyncTime;
    //网价修改人
    private String netPriceModifyBy;
    //    LimitState
    private Integer limitState;
    //            ManyID
    private String manyId;
    //            多次净重
    private Double multiNetWeight;
}
