package com.calcifer.weight.controller;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.UserRolePO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/********
 * 用户-角色
 *
 */
@Slf4j
@RequestMapping("/sys/userRole")
@RestController
@Api(basePath = "/sys/userRole", value = "系统 用户-角色模块", description = "系统", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;


    /**
     * 描述:  添加
     */
    @PostMapping(value = "/add.do")
    @ApiOperation(value = "添加", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> add(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "用户id") @RequestParam(required = true) String userId,
            @ApiParam(required = true, value = "角色Ids序列 中间用逗号隔开") @RequestParam(required = true) String roleIds,
            HttpServletRequest request) {
        Integer flag = userRoleService.addRole(userId, roleIds);//执行保存
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
        } else {
            return new RespWrapper<>(false, RespCodeEnum.ERROR);
        }
    }

    /**
     * 描述:  初始化人员角色
     * 名称:  listUserRole
     */
    @PostMapping(value = "/listAll.do")
    @ApiOperation(value = "用户的角色", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<List<UserRolePO>> listAll(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "用户id") @RequestParam(required = true) String userId,
            HttpServletRequest request) {
        List<UserRolePO> userRolePOList = userRoleService.queryUserRole(userId);//执行保存
        return new RespWrapper<>(userRolePOList, RespCodeEnum.SUCCESS, "操作成功");
    }

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
