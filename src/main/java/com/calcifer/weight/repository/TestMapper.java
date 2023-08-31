package com.calcifer.weight.repository;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface TestMapper {

    @Select("select * from sys_user")
    List<HashMap<String, Object>> queryTest();
}
