package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.UserDTO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("auth")
@Tag(name = "认证", description = "认证")
public class AuthController {
    @Autowired
    private UserService userService;


    @Operation(summary = "登录", description = "登录")
    @PostMapping("login")
    public RespWrapper<?> login(
            @Parameter(description = "用户名", required = true) String username,
            @Parameter(description = "密码", required = true) String password,
            HttpServletRequest request) {
        UserDTO userDTO = userService.queryUser(username, password);
        if (userDTO == null) {
            return new RespWrapper<>(false, RespCodeEnum.USERNAME_NOT_FOUND);
        }
        if (userDTO.getStatus() == UserStatusEnum.FORBIDDEN) {
            return new RespWrapper<>(false, RespCodeEnum.USER_LOCKED_ERROR);
        }
        request.getSession().setAttribute("user", userDTO);

        return new RespWrapper<>(userDTO);
    }

    @Operation(summary = "登出", description = "登出")
    @PostMapping("logout")
    public RespWrapper<?> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }
}
