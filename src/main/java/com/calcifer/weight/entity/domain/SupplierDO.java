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
* 
* @TableName gysxx
*/
@TableName("gysxx")
@Data
public class SupplierDO extends Model<SupplierDO> {
    private static final long serialVersionUID = 3219638680718443640L;


    @NotBlank(message="[]不能为空")
    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableId("供应商名称")
    private String supplierName;

    @NotNull(message="[]不能为空")
    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer id;

    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("供货物料名称")
    private String supplyMaterialName;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("公司地址")
    private String corpAddress;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("联系人")
    private String contactPerson;

    @Size(max= 15,message="编码长度不能超过15")
    @Length(max= 15,message="编码长度不能超过15")
    @TableField("电话")
    private String phone;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("备注")
    private String comment;
}
