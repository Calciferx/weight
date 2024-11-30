package com.calcifer.weight.autoweigh;

import com.xiaoleilu.hutool.util.HexUtil;
import org.junit.jupiter.api.Test;

class AutoScanJobTest {

    @Test
    public void TestHex() {
        boolean test = (HexUtil.decodeHex("38".toCharArray())[0] & 0b00001000) == 0;
        System.out.println(test);
    }

}