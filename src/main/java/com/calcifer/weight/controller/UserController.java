package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.entity.po.UserRolePO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserRoleService;
import com.calcifer.weight.service.UserService;
import com.calcifer.weight.utils.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.calcifer.weight.common.WeightContext.TOKEN_USER_MAP;


/**
 * 描述: 用户
 */

@Slf4j
@RequestMapping("/sys/user")
@RestController
@Tag(name = "系统 用户模块", description = "系统")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 描述:  添加
     * 名称:  add
     */
    @PostMapping(value = "/add.do")
    @Operation(summary = "添加", description = "条件：无")
    public RespWrapper<Boolean> add(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "用户名", required = true) @RequestParam(required = true) String name,
            @Parameter(description = "密码", required = true) @RequestParam(required = true) String pwd,
            @Parameter(description = "姓名", required = true) @RequestParam(required = true) String realName,
            @Parameter(description = "角色ID", required = true) @RequestParam(required = true) String roleIds,
//    		@Parameter(description = "用户分组ID", required = true) @RequestParam(required = true) String groupId,
            @Parameter(description = "联系方式") @RequestParam(required = false) String phone,
            @Parameter(description = "是否启用（0禁用，1启用）", required = true) @RequestParam(required = true) UserStatusEnum status,
            HttpServletRequest request
    ) {
        UserPO userPO = new UserPO(UUID.randomUUID().toString(), DigestUtils.md5DigestAsHex(pwd.getBytes()), name, phone, status, DateUtil.getTime(), realName, null, null, null, roleIds);
        Integer flag = userService.addUser(userPO);//执行保存
        if (flag > 0) {
            // 添加角色
            Integer rflag = userRoleService.addRole(userPO.getId(), roleIds);//执行保存
            if (rflag < 1) {
                return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功，请添加用户角色");
            }
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
        } else {
            return new RespWrapper<>(false, RespCodeEnum.ERROR);
        }
    }

    /**
     * 密码验证
     */
    @PostMapping(value = "/verification.do")
    @Operation(summary = "密码验证", description = "条件：无")
    public RespWrapper<Boolean> verification(@Parameter(description = "token", required = true) @RequestParam(required = true) String token,
                                             @Parameter(description = "密码", required = true) @RequestParam(required = true) String pwd,
                                             HttpServletRequest request
    ) {
        User user = TOKEN_USER_MAP.get(token);
        // 获取当前用户的密码
        UserPO userPO = userService.queryUserById(user.getId());
        if (userPO != null) {
            String realPwd = userPO.getPwd();
            if (realPwd.equals(DigestUtils.md5DigestAsHex(pwd.getBytes()))) {
                return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "密码正确");
            }
        }
        return new RespWrapper<>(false, RespCodeEnum.ERROR, "密码不争取");
    }

    /**
     * 描述:  编辑
     * 名称:  edit
     */
    @PostMapping(value = "/update.do")
    @Operation(summary = "修改", description = "条件：无")
    public RespWrapper<Boolean> update(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "用户id", required = true) @RequestParam(required = true) String userId,
            @Parameter(description = "用户名") @RequestParam(required = false) String name,
            @Parameter(description = "密码") @RequestParam(required = false) String pwd,
            @Parameter(description = "用户分组ID") @RequestParam(required = false) String groupId,
            @Parameter(description = "联系方式") @RequestParam(required = false) String phone,
            @Parameter(description = "姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "营销区域id") @RequestParam(required = false) String areaId,
            @Parameter(description = "是否启用（0禁用，1启用）") @RequestParam(required = false) UserStatusEnum status,
            HttpServletRequest request) {
        UserPO userPO = new UserPO(userId, DigestUtils.md5DigestAsHex(pwd.getBytes()), name, phone, status, null, realName, areaId, null, null, null);
        Integer flag = userService.update(userPO);//执行保存
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
        } else {
            return new RespWrapper<>(false, RespCodeEnum.ERROR, "操作失败");
        }
    }

    /**
     * 描述:  删除
     * 名称:  del
     */
    @PostMapping(value = "/delete.do")
    @Operation(summary = "删除", description = "条件：无")
    public RespWrapper<Boolean> delete(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "用户id", required = true) @RequestParam(required = true) String userId,
            HttpServletRequest request) {
        Integer flag = userService.delete(userId);
        if (flag > 0) {
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
        } else {
            return new RespWrapper<>(false, RespCodeEnum.ERROR, "操作失败");
        }
    }

    /**
     * 描述:  列表
     * 名称:  list
     */
    @PostMapping(value = "/pageList.do")
    @Operation(summary = "列表（分页）", description = "条件：无")
    public RespWrapper<List<UserPO>> pageList(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "角色Id") @RequestParam(required = false) String roleId,
//			@Parameter(description = "页码", required = true)@RequestParam(value="page") Integer pageNum,
//			@Parameter(description = "每页条数", required = true)@RequestParam(value="pageSize") Integer pageSize,
            HttpServletRequest request) {
        List<String> userIds = null;
        if (roleId != null) {
            List<UserRolePO> userRolePOList = userRoleService.queryUserRole(null, roleId, null);
            userIds = userRolePOList.stream().map(UserRolePO::getUserId).collect(Collectors.toList());
        }
        List<UserPO> userPOS = userService.queryUserByIds(userIds);
        return new RespWrapper<>(userPOS, RespCodeEnum.SUCCESS, "操作成功");
    }

    /**
     * 分组列表查询用户信息
     */
    @PostMapping(value = "/findList.do")
    @Operation(summary = "列表（分页）", description = "条件：无")
    public RespWrapper<List<UserPO>> findList(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "分作ID") @RequestParam(required = false) String groupId,
            @Parameter(description = "页码", required = true) @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        PageInfo<UserPO> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> userService.queryUserByIds(null));
        if (pageInfo != null) {
            return new RespWrapper<>(pageInfo.getList(), RespCodeEnum.SUCCESS, (int) pageInfo.getTotal(), pageInfo.getPages(), "操作成功");
        }
        return new RespWrapper<>(null, RespCodeEnum.ERROR, 0, 0, "操作失败");
    }

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}