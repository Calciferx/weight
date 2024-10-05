package com.calcifer.weight.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.UsersDO;
import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.domain.UserPO;
import com.calcifer.weight.repository.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, UsersDO> implements IService<UsersDO> {

    public User queryUser(String name, String pwd) {
        UsersDO usersDO = lambdaQuery().eq(UsersDO::getUsername, name).one();
        if (usersDO != null && usersDO.getPassword().trim().equals(pwd)) {
            User user = new User();
            BeanUtils.copyProperties(usersDO, user);
            return user;
        }
        return null;
    }

}
