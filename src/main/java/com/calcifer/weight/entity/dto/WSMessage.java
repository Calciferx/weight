package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.WSCodeEnum;
import lombok.Data;

@Data
public class WSMessage {
    private WSCodeEnum type;
    private Object map;

    public WSMessage() {
    }

    public WSMessage(WSCodeEnum type, Object map) {
        this.type = type;
        this.map = map;
    }
}
