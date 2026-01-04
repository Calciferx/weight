package com.calcifer.weight.controller;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.UserRolePO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/********
 * 用户-角色
 *
 */
@Slf4j
@RequestMapping("/sys/userRole")
@RestController
@Tag(name = "系统 用户-角色模块", description = "系统")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;


    /**
     * 描述:  添加
     */
    @PostMapping(value = "/add.do")
    @Operation(summary = "添加", description = "条件：无")
    public RespWrapper<Boolean> add(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "用户id", required = true) @RequestParam(required = true) String userId,
            @Parameter(description = "角色Ids序列 中间用逗号隔开", required = true) @RequestParam(required = true) String roleIds,
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
    @Operation(summary = "用户的角色", description = "条件：无")
    public RespWrapper<List<UserRolePO>> listAll(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "用户id", required = true) @RequestParam(required = true) String userId,
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