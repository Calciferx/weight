package com.calcifer.weight.service;

import com.xiaoleilu.hutool.date.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class WeightRecordServiceTest {
    @Autowired
    private WeightRecordService weightRecordService;

    @Test
    void generateWeighId() {
        String syj = weightRecordService.generateWeighId("SYJ");
        System.out.println(syj);
    }

    @Test
    void dateStrTest() {
        String dateStr = DateUtil.format(new Date(), "yyMM");
        System.out.println(dateStr);
    }

    @Test
    void numStrTest() {
        String numStr = "CY2012001                     ".trim().replaceAll("[a-zA-Z]", "").substring(4);
        int num = Integer.parseInt(numStr);
        String incNumStr = String.format("%03d", num + 1);
        System.out.printf("%3d", 2);
        System.out.println(incNumStr);
    }
}