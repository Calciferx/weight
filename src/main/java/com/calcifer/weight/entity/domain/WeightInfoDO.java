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
import javax.validation.constraints.Size;
import java.util.Date;

/**
*
 * @TableName jjxx 预置检斤信息
*/
@Data
@TableName("jjxx")
public class WeightInfoDO extends Model<WeightInfoDO> {

    private static final long serialVersionUID = 6501711200365582716L;

    @NotBlank(message="[]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    @TableId("检斤编号")
    private String weighId;

    @NotNull(message="[]不能为空")
    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer id;

    @NotBlank(message="[]不能为空")
    @Size(max= 100,message="编码长度不能超过100")
    @Length(max= 100,message="编码长度不能超过100")
    @TableField("供应商名称")
    private String supplierName;

    @NotBlank(message="[]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    @TableField("物料名称")
    private String materialName;

    @NotBlank(message="[]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("车牌号")
    private String plateNumber;

    @NotBlank(message="[]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("检斤员")
    private String weighMan;

    @NotBlank(message="[]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("审核员")
    private String auditor;

    @NotBlank(message="[]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("检斤状态")
    private String weighStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("检斤日期")
    private Date weighDate;

    @NotBlank(message="[]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    @TableField("受控编号")
    private String controlId;

    @NotBlank(message="[]不能为空")
    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("是否化验")
    private String isTest;

    @Size(max= 100,message="编码长度不能超过100")
    @Length(max= 100,message="编码长度不能超过100")
    @TableField("备注")
    private String comment;

    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("物料名称简码")
    private String materialCode;

}
