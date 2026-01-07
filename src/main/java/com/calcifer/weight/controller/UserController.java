package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.UserDTO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserRoleService;
import com.calcifer.weight.service.UserService;
import com.calcifer.weight.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("user")
@Tag(name = "用户管理", description = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;


    @PostMapping(value = "/add")
    @Operation(summary = "添加用户", description = "添加用户")
    public RespWrapper<?> add(@RequestBody UserDTO userDTO) {
        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(userDTO, userPO);
        userPO.setId(UUID.randomUUID().toString());
        userPO.setPwd(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        userPO.setCreateTime(DateUtil.getTime());

        Integer flag = userService.addUser(userPO);
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        } else {
            return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
    }

    @PostMapping(value = "/update")
    @Operation(summary = "修改用户信息", description = "修改用户信息")
    public RespWrapper<?> update(@RequestBody UserDTO userDTO) {
        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(userDTO, userPO);
        userPO.setId(UUID.randomUUID().toString());
        userPO.setPwd(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        userPO.setCreateTime(DateUtil.getTime());

        Integer flag = userService.update(userPO);
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        } else {
            return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
    }

    @PostMapping(value = "/delete")
    @Operation(summary = "删除用户", description = "删除用户")
    public RespWrapper<Boolean> delete(@RequestBody UserDTO userDTO) {
        Integer flag = userService.delete(userDTO.getUserId());
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        } else {
            return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
    }

    @PostMapping(value = "/query")
    @Operation(summary = "查询用户信息", description = "查询用户信息")
    public RespWrapper<List<UserPO>> pageList(@RequestBody UserDTO userDTO) {
        List<UserPO> userPOS = userService.queryUserByIds(List.of(userDTO.getUserId()));
        return new RespWrapper<>(userPOS);
    }

}