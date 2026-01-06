package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.dto.UserDTO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.po.UserRolePO;
import com.calcifer.weight.entity.po.UserSlaveInfo;
import com.calcifer.weight.entity.vo.LoginVO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.SlaveDetailService;
import com.calcifer.weight.service.UserRoleService;
import com.calcifer.weight.service.UserService;
import com.calcifer.weight.service.UserSlaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.calcifer.weight.common.WeightContext.NAME_TOKEN_MAP;
import static com.calcifer.weight.common.WeightContext.TOKEN_USER_MAP;

@Slf4j
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserSlaveService userSlaveService;
    @Autowired
    private SlaveDetailService slaveDetailService;
    @Autowired
    private UserRoleService userRoleService;


    @PostMapping("login")
    public RespWrapper<?> login(String username, String password) {
        UserDTO userDTO = userService.queryUser(username, password);
        if (userDTO == null) {
            return new RespWrapper<>(RespCodeEnum.IS_NOT_USERNAME_ERROR);
        }
        if (userDTO.getStatus() == UserStatusEnum.FORBIDDEN) {
            return new RespWrapper<>(RespCodeEnum.USER_LOCK_ERROR);
        }
        // 如果已登录则移除之前的登录信息
        if (NAME_TOKEN_MAP.containsKey(username)) {
            String token = NAME_TOKEN_MAP.get(username);
            NAME_TOKEN_MAP.remove(username);
            TOKEN_USER_MAP.remove(token);
        }
        // 重新设置新的登录信息
        String token = UUID.randomUUID().toString();
        log.info("user: {}, token: {}", username, token);
        NAME_TOKEN_MAP.put(username, token);
        TOKEN_USER_MAP.put(token, userDTO);

        UserSlaveInfo userSlaveInfo = userSlaveService.queryUserSlaveInfo(userDTO.getUserId());
        List<SlaveDetailInfo> slaveDetailInfos = new ArrayList<>();
        if (userSlaveInfo != null) {
            slaveDetailInfos = slaveDetailService.querySlaveDetailInfo(userSlaveInfo.getSlaveId(), null, null);
        }
        List<UserRolePO> userRolePOS = userRoleService.queryUserRole(userDTO.getUserId());
        LoginVO loginVO = new LoginVO(token, userDTO, userRolePOS, slaveDetailInfos);
        return new RespWrapper<>(loginVO);
    }

    @PostMapping("logout")
    public RespWrapper<Object> logout() {
        return new RespWrapper<>(RespCodeEnum.IS_NOT_LOGIN_ERROR);
    }
}
