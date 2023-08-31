package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.enums.WSCodeEnum;
import lombok.Data;

@Data
public class WSRespWrapper<T> {
    private T data;
    private WSCodeEnum msgType;
    private String msg;


    public WSRespWrapper(T data, WSCodeEnum msgType) {
        this.data = data;
        this.msgType = msgType;
        this.msg = msgType.getMsg();
    }

    public WSRespWrapper(T data) {
        this(data, WSCodeEnum.MSGType_1);
    }

    public WSRespWrapper(WSCodeEnum msgType) {
        this(null, msgType);
    }

    public WSRespWrapper(T data, WSCodeEnum msgType, String msg) {
        this.data = data;
        this.msgType = msgType;
        this.msg = msg;
    }
}
