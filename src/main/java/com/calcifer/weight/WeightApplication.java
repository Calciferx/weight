package com.calcifer.weight;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication
@MapperScan("com.calcifer.weight.repository")
public class WeightApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeightApplication.class, args);
    }
}
