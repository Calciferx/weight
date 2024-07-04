package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.WSCodeEnum;
import lombok.Data;

@Data
public class WSRespWrapper<T> {
    private WSCodeEnum type;
    private T data;
    private String msg;


    public WSRespWrapper(T data, WSCodeEnum type) {
        this.data = data;
        this.type = type;
        this.msg = type.getMsg();
    }

    public WSRespWrapper(T data, WSCodeEnum type, String msg) {
        this.data = data;
        this.type = type;
        this.msg = msg;
    }
}
