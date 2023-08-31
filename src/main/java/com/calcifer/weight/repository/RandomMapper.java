package com.calcifer.weight.repository;

import org.apache.ibatis.annotations.MapKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RandomMapper {

    @MapKey("dictNo")
    List<Map> loadBySql(Map map);

    int update(Map map);

    int add(Map map);
}
