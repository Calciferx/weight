package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.WSMessageTypeEnum;
import lombok.Data;

@Data
public class WSMessage {
    private WSMessageTypeEnum type;
    private Object map;

    public WSMessage() {
    }

    public WSMessage(WSMessageTypeEnum type, Object map) {
        this.type = type;
        this.map = map;
    }
}
