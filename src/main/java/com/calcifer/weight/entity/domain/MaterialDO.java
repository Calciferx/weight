package com.calcifer.weight.entity.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @TableName wlxx
 */
@Data
@TableName("wlxx")
public class MaterialDO extends Model<MaterialDO> {
    private static final long serialVersionUID = -2352227558167156187L;
    @NotBlank(message = "[]不能为空")
    @Size(max = 20, message = "编码长度不能超过20")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableId("物料编码")
    private String materialId;

    @NotNull(message = "[]不能为空")
    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer id;

    @Size(max = 20, message = "编码长度不能超过20")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("物料名称")
    private String materialName;

    @Size(max = 10, message = "编码长度不能超过10")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField("规格型号")
    private String specifications;

    @Size(max = 10, message = "编码长度不能超过10")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField("计量单位")
    private String weightUnit;

    @Size(max = 20, message = "编码长度不能超过20")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("备注")
    private String comment;

    @Size(max = 10, message = "编码长度不能超过10")
    @Length(max = 10, message = "编码长度不能超过10")
    @TableField("是否化验")
    private String isTest;

}
