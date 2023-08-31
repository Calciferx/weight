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

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 描述:  添加
     * 名称:  add
     */
    @PostMapping(value = "/add.do")
    @ApiOperation(value = "添加", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> add(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "用户名") @RequestParam(required = true) String name,
            @ApiParam(required = true, value = "密码") @RequestParam(required = true) String pwd,
            @ApiParam(required = true, value = "姓名") @RequestParam(required = true) String realName,
            @ApiParam(required = true, value = "角色ID") @RequestParam(required = true) String roleIds,
//    		@ApiParam(required = true, value = "用户分组ID") @RequestParam(required = true) String groupId,
            @ApiParam(required = false, value = "联系方式") @RequestParam(required = false) String phone,
            @ApiParam(required = true, value = "是否启用（0禁用，1启用）") @RequestParam(required = true) UserStatusEnum status,
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
    @ApiOperation(value = "密码验证", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> verification(@ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
                                             @ApiParam(required = true, value = "密码") @RequestParam(required = true) String pwd,
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
    @ApiOperation(value = "修改", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> update(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "用户id") @RequestParam(required = true) String userId,
            @ApiParam(required = false, value = "用户名") @RequestParam(required = false) String name,
            @ApiParam(required = false, value = "密码") @RequestParam(required = false) String pwd,
            @ApiParam(required = false, value = "用户分组ID") @RequestParam(required = false) String groupId,
            @ApiParam(required = false, value = "联系方式") @RequestParam(required = false) String phone,
            @ApiParam(required = false, value = "姓名") @RequestParam(required = false) String realName,
            @ApiParam(required = false, value = "营销区域id") @RequestParam(required = false) String areaId,
            @ApiParam(required = false, value = "是否启用（0禁用，1启用）") @RequestParam(required = false) UserStatusEnum status,
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
    @ApiOperation(value = "删除", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> delete(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "用户id") @RequestParam(required = true) String userId,
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
    @ApiOperation(value = "列表（分页）", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<List<UserPO>> pageList(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = false, value = "角色Id") @RequestParam(required = false) String roleId,
//			@ApiParam(required = true,value = "页码", defaultValue ="1")@RequestParam(value="page") Integer pageNum,
//			@ApiParam(required = true,value = "每页条数", defaultValue="10")@RequestParam(value="pageSize") Integer pageSize,
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
    @ApiOperation(value = "列表（分页）", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<List<UserPO>> findList(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = false, value = "分作ID") @RequestParam(required = false) String groupId,
            @ApiParam(required = true, value = "页码", defaultValue = "1") @RequestParam(value = "page") Integer pageNum,
            @ApiParam(required = true, value = "每页条数", defaultValue = "10") @RequestParam(value = "pageSize") Integer pageSize,
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
