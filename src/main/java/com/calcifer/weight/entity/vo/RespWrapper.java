package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespWrapper<T> {
    private Boolean success;
    private T data;
    private String errorCode;
    private String errorMessage;
    private Integer showType;
    private String traceId;
    private String host;

    public RespWrapper() {}

    public RespWrapper(Boolean success, String errorCode, String errorMessage) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public RespWrapper(Boolean success, RespCodeEnum respCodeEnum) {
        this.success = success;
        this.errorCode = respCodeEnum.getCode().toString();
        this.errorMessage = respCodeEnum.getMsg();

    }

    public RespWrapper(Boolean success, RespCodeEnum respCodeEnum, String errorMessage) {
        this.success = success;
        this.errorCode = respCodeEnum.getCode().toString();
        this.errorMessage = errorMessage;
    }

    public RespWrapper(T data) {
        this.success = true;
        this.data = data;
        this.errorCode = RespCodeEnum.SUCCESS.getCode().toString();
        this.errorMessage = RespCodeEnum.SUCCESS.getMsg();
    }
}
