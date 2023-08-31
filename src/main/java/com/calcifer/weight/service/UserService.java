package com.calcifer.weight.service;

import com.calcifer.weight.entity.dto.User;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.repository.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryUser(String name, String pwd) {
        UserPO userPO = new UserPO(name);
        userPO = userMapper.queryUser(userPO);
        if (userPO != null && userPO.getPwd().equalsIgnoreCase(DigestUtils.md5DigestAsHex(pwd.getBytes()))) {
            User user = new User();
            BeanUtils.copyProperties(userPO, user);
            return user;
        }
        return null;
    }

    public Integer addUser(UserPO userPO) {
        return userMapper.addUser(userPO);
    }

    public List<UserPO> queryUserByIds(List<String> userIds) {
        return userMapper.queryUserByIds(userIds);
    }

    public UserPO queryUserById(String userId) {
        UserPO userPO = new UserPO();
        userPO.setId(userId);
        return userMapper.queryUser(userPO);
    }

    public Integer update(UserPO userPO) {
        return userMapper.update(userPO);
    }

    public Integer delete(String id) {
        return userMapper.delete(id);
    }
}
