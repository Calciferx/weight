package com.calcifer.weight.repository;

import com.calcifer.weight.entity.po.TruckInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface CardMapper {
    TruckInfo getTruckInfo(String cardNum);
}
