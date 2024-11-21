package com.calcifer.weight.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @TableName kpxx
 */
@Data
@TableName("kpxx")
public class CardInfoDO extends Model<CardInfoDO> {

    private static final long serialVersionUID = -8397338802830187921L;

    @NotNull(message = "[]不能为空")
    @TableId(value = "序号", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "[]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField("卡号")
    private String cardNum;

    @NotBlank(message = "[]不能为空")
    @Size(max = 20, message = "编码长度不能超过20")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("物料名称")
    private String materialName;

    @NotBlank(message = "[]不能为空")
    @Size(max = 20, message = "编码长度不能超过20")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("物料编码")
    private String materialId;


}
