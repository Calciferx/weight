package com.calcifer.weight.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calcifer.weight.entity.po.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<UserPO> {
}
