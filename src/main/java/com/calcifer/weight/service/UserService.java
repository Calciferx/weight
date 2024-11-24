package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.UserDO;
import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.repository.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, UserDO> {

    public User queryUser(String name, String pwd) {
        UserDO userDO = lambdaQuery().eq(UserDO::getUsername, name).one();
        if (userDO != null && userDO.getPassword().trim().equals(pwd)) {
            User user = new User();
            BeanUtils.copyProperties(userDO, user);
            return user;
        }
        return null;
    }

}
