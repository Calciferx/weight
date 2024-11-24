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
import java.util.Date;

/**
* 
* @TableName clxx 车辆信息
*/
@Data
@TableName("clxx")
public class CarDO extends Model<CarDO> {
    private static final long serialVersionUID = 7670123218847319545L;

    @NotBlank(message="[]不能为空")
    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableId("车牌号")
    private String plateNumber;

    @NotNull(message="[]不能为空")
    @TableField(value = "序号", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer Id;

    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("车辆编号")
    private String carId;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("车辆类型")
    private String carType;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("车辆型号")
    private String carModel;

    @TableField("载重量")
    private Double weightCapacity;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("车主")
    private String carOwner;

    @Size(max= 15,message="编码长度不能超过15")
    @Length(max= 15,message="编码长度不能超过15")
    @TableField("电话")
    private String phone;

    @Size(max= 20,message="编码长度不能超过20")
    @Length(max= 20,message="编码长度不能超过20")
    @TableField("备注")
    private String comment;

    @TableField("登记时间")
    private Date assignTime;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("运输物料名称")
    private String carryMaterialName;

}
