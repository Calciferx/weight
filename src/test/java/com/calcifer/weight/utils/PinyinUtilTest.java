package com.calcifer.weight.utils;

import org.junit.jupiter.api.Test;

class PinyinUtilTest {

    @Test
    void getFirstLetters() {
        String code = PinyinUtil.getFirstLetters("石油焦");
        System.out.println(code);
    }
}