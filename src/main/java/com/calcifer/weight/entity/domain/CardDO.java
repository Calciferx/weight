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
 * @TableName kpxx
 */
@Data
@TableName("kpxx")
public class CardDO extends Model<CardDO> {

    private static final long serialVersionUID = -8397338802830187921L;

    @NotNull(message = "[]不能为空")
    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer id;

    @NotBlank(message = "[]不能为空")
    @Size(max = 100, message = "编码长度不能超过100")
    @Length(max = 100, message = "编码长度不能超过100")
    @TableId("卡号")
    private String cardNum;

    @NotBlank(message = "[]不能为空")
    @Size(max = 100, message = "编码长度不能超过100")
    @Length(max = 100, message = "编码长度不能超过100")
    @TableField("数据")
    private String data;

    @NotBlank(message = "[]不能为空")
    @Size(max = 50, message = "编码长度不能超过50")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField("数据类型")
    private String dataType;


}
