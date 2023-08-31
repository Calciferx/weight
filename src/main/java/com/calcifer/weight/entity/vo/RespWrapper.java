package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import lombok.Data;

@Data
public class RespWrapper<T> {
    private T data;
    private RespCodeEnum code;
    private String msg;
    private int total;
    private int pages;

    public RespWrapper() {
    }

    public RespWrapper(T data, RespCodeEnum code) {
        this.data = data;
        this.code = code;
        this.msg = code.getMsg();
    }

    public RespWrapper(T data, RespCodeEnum code, int total, int pages) {
        this(data, code);
        this.total = total;
        this.pages = pages;
    }

    public RespWrapper(T data, RespCodeEnum code, int total, int pages, String msg) {
        this(data, code, total, pages);
        this.msg = msg;
    }

    public RespWrapper(T data) {
        this(data, RespCodeEnum.SUCCESS);
    }

    public RespWrapper(RespCodeEnum code) {
        this(null, code);
    }

    public RespWrapper(T data, RespCodeEnum code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }
}
