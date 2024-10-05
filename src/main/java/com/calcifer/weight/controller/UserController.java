package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.UsersDO;
import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.domain.UserPO;
import com.calcifer.weight.entity.domain.UserRolePO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserRoleService;
import com.calcifer.weight.service.UserService;
import com.calcifer.weight.utils.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.calcifer.weight.WeightApplication.TOKEN_USER_MAP;

/**
 * 描述: 用户
 */

@Slf4j
@RequestMapping("/sys/user")
@RestController
@Api(basePath = "/sys/user", value = "系统 用户模块", description = "系统", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class UserController {

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
