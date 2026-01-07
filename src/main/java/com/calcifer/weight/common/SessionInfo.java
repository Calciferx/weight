package com.calcifer.weight.common;

import com.calcifer.weight.entity.dto.UserDTO;
import lombok.Data;

@Data
public class SessionInfo {
    private String token;
    private UserDTO userDTO;

}
