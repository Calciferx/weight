package com.calcifer.weight.entity.vo;

import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.po.UserRolePO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private User info;
    private List<UserRolePO> roleList;
    private List<SlaveDetailInfo> slaveList;
}
