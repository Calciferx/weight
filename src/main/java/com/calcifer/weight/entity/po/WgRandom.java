package com.calcifer.weight.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wg_random")
public class WgRandom {
    private Integer randomNum;
    private String type;
    private String createDay;
    private String dictName;
    private String dictNoPrefix;
}
