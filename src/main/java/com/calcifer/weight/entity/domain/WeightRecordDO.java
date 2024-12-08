package com.calcifer.weight.entity.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
* 
* @TableName qchjj
*/
@Data
@TableName("qchjj")
public class WeightRecordDO extends Model<WeightRecordDO> {
    private static final long serialVersionUID = 4958187847174470709L;

    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @NotNull(message = "[]不能为空")
    private Integer id;

    @TableId("检斤编号")
    @NotBlank(message="[]不能为空")
    @Length(max= 30,message="编码长度不能超过30")
    private String weighId;

    @TableField("物料编码")
    @Length(max= 20,message="编码长度不能超过20")
    private String materialId;

    @TableField("供应商名称")
    @Length(max= 20,message="编码长度不能超过20")
    private String supplierName;

    @TableField("物料名称")
    @Length(max= 20,message="编码长度不能超过20")
    private String materialName;

    @TableField("车牌号")
    @Length(max= 10,message="编码长度不能超过10")
    private String plateNumber;

    @TableField("毛重")
    private Double roughWeight;

    @TableField("皮重")
    private Double tareWeight;

    @TableField("净重")
    private Double netWeight;

    @TableField("检斤日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date weighDate;

    @TableField("检斤状态")
    @Length(max= 10,message="编码长度不能超过10")
    private String weighStatus;

    @TableField("是否打印")
    @Length(max= 10,message="编码长度不能超过10")
    private String isPrint;

    @TableField("受控编号")
    @Length(max= 30,message="编码长度不能超过30")
    private String controlId;

    @TableField("备注")
    @Length(max= 50,message="编码长度不能超过50")
    private String comment;

    @TableField("检斤员")
    @Length(max= 10,message="编码长度不能超过10")
    private String weighMan;

    @TableField("复合员")
    @Length(max= 10,message="编码长度不能超过10")
    private String checkMan;

    @TableField("审核员")
    @Length(max= 10,message="编码长度不能超过10")
    private String auditor;

    @TableField("样品状态")
    @Length(max= 10,message="编码长度不能超过10")
    private String sampleStatus;

    @TableField("送样时间")
    private Date sampleDeliverTime;

    @TableField("取送样人")
    @Length(max= 10,message="编码长度不能超过10")
    private String sampleDeliverMan;

    @TableField("收样人")
    @Length(max= 10,message="编码长度不能超过10")
    private String sampleAcceptMan;

    @TableField("分析时间")
    private Date analyseTime;

    @TableField("是否分析")
    @Length(max= 10,message="编码长度不能超过10")
    private String isAnalyse;

    @TableField("是否化验")
    @Length(max= 10,message="编码长度不能超过10")
    private String isTest;

    @TableField("车辆类型")
    @Length(max= 10,message="编码长度不能超过10")
    private String carType;

    @TableField("发站")
    @Length(max= 20,message="编码长度不能超过20")
    private String carFrom;

    @TableField("取样时间")
    private Date sampleExtractTime;

    @TableField("到车时间")
    private Date carArriveTime;

    @TableField("产品名称")
    @Length(max= 20,message="编码长度不能超过20")
    private String productName;

    @TableField("简称")
    @Length(max= 10,message="编码长度不能超过10")
    private String shortName;

    @TableField("规格型号")
    @Length(max= 30,message="编码长度不能超过30")
    private String specifications;

    @TableField("块数")
    private Integer pieceNumber;

    @TableField("毛重时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date roughWeightTime;

    @TableField("皮重时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tareWeightTime;
    
}
