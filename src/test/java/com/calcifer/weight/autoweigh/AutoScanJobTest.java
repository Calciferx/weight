package com.calcifer.weight.autoweigh;

import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

class AutoScanJobTest {

    @Test
    public void TestHex() {
        boolean test = (HexUtil.decodeHex("38".toCharArray())[0] & 0b00001000) == 0;
        System.out.println(test);
    }

}