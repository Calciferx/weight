package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageWrapper<T> {
    private List<T> list;
    private Integer current;
    private Integer pageSize;
    private Integer total;

    public PageWrapper() {
    }

}
