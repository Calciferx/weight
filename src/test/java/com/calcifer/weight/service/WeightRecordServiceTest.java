package com.calcifer.weight.service;

import com.xiaoleilu.hutool.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

class WeightRecordServiceTest {

    @Test
    void generateWeighId() {

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