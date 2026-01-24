package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.dto.UserDTO;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.repository.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, UserPO> {

    public UserDTO queryUser(String username, String password) {
        UserPO userPO = lambdaQuery().eq(UserPO::getUsername, username).one();
        if (userPO != null && userPO.getPassword() != null && userPO.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes()))) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(userPO, userDTO);
            return userDTO;
        }
        return null;
    }

    public Integer addUser(UserPO userPO) {
        return save(userPO) ? 1 : 0;
    }

    public List<UserPO> queryUserByIds(List<String> userIds) {
        return baseMapper.selectBatchIds(userIds);
    }

    public UserPO queryUserById(String userId) {
        return getById(userId);
    }

    public Integer update(UserPO userPO) {
        return updateById(userPO) ? 1 : 0;
    }

    public Integer delete(String id) {
        return removeById(id) ? 1 : 0;
    }
}
