package com.calcifer.weight.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calcifer.weight.entity.domain.UserPO;
import com.calcifer.weight.entity.domain.UsersDO;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserMapper extends BaseMapper<UsersDO> {

}
