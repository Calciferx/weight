package com.calcifer.weight;


import com.calcifer.weight.entity.dto.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.calcifer.weight.repository")
public class WeightApplication {
    public static final ConcurrentHashMap<String, String> NAME_TOKEN_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, User> TOKEN_USER_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(WeightApplication.class, args);
    }
}
