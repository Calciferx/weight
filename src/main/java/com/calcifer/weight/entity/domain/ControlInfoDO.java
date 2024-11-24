package com.calcifer.weight.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
* 
* @TableName skbh
*/
@Data
@TableName("skbh")
public class ControlInfoDO extends Model<ControlInfoDO> {

    private static final long serialVersionUID = -5487824201096102980L;

    @NotNull(message="[]不能为空")
    @TableId(value = "序号", type = IdType.AUTO)
    private Integer id;

    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    @TableField("名称")
    private String name;

    @TableField("受控编号")
    private String controlId;

    @TableField("登记时间")
    private Date assignTime;
}
