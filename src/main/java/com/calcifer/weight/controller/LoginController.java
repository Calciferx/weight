package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.dto.MenuItem;
import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.vo.LoginVO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.UserService;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.calcifer.weight.common.WeightContext.NAME_TOKEN_MAP;
import static com.calcifer.weight.common.WeightContext.TOKEN_USER_MAP;

@Slf4j
@RestController
@RequestMapping("adminx")
public class LoginController {

    @Autowired
    private UserService userService;
    @Value("${calcifer.weight.index-menu}")
    private String indexMenuPath;

    @PostMapping("login/login.do")
    public RespWrapper<LoginVO> login(String username, String password) {
        User user = userService.queryUser(username, password);
        if (user == null) {
            return new RespWrapper<>(RespCodeEnum.IS_NOT_USERNAME_ERROR);
        }
        if (user.getStatus() == UserStatusEnum.FORBIDDEN) {
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
        TOKEN_USER_MAP.put(token, user);

        LoginVO loginVO = new LoginVO(token, user);
        return new RespWrapper<>(loginVO);
    }

    @PostMapping("login/notAuth")
    public RespWrapper<Object> notAuth() {
        return new RespWrapper<>(RespCodeEnum.IS_NOT_LOGIN_ERROR);
    }

    @RequestMapping("indexMenu")
    public RespWrapper<List<MenuItem>> getIndexMenu(String token) {
        if (!TOKEN_USER_MAP.containsKey(token)) {
            return new RespWrapper<>(RespCodeEnum.ERROR);
        }
        User user = TOKEN_USER_MAP.get(token);
        String menuJsonArrayStr = FileUtil.readUtf8String(indexMenuPath);
        List<MenuItem> menuItems = JSON.parseArray(menuJsonArrayStr, MenuItem.class);
        if ("zhangjie".equals(user.getName().trim())) {
            return new RespWrapper<>(menuItems, RespCodeEnum.SUCCESS);
        } else {
            List<MenuItem> menu = menuItems.stream().filter(item -> item.getAuth() < 2).collect(Collectors.toList());
            return new RespWrapper<>(menu, RespCodeEnum.SUCCESS);
        }
    }
}
