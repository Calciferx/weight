package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.po.WgRandom;
import com.calcifer.weight.repository.RandomMapper;
import org.springframework.stereotype.Service;

@Service
public class RandomService extends ServiceImpl<RandomMapper, WgRandom> {

    /**
     * 随机数
     *
     * @return
     */
    public String randomUtils(String dictType, String monthStr) {
        String distributorNo = "";
        WgRandom random = lambdaQuery()
                .eq(WgRandom::getType, dictType)
                .eq(WgRandom::getCreateDay, monthStr)
                .one();

        if (random != null) {
            String num = (random.getRandomNum() + 1) + "";
            String no = "";
            switch (num.length()) {
                case 1:
                    no = "000" + num;
                    break;
                case 2:
                    no = "00" + num;
                    break;
                case 3:
                    no = "0" + num;
                    break;
                case 4:
                    no = num;
                    break;
                default:
                    no = num;
            }
            distributorNo = random.getDictNoPrefix() + monthStr + no;
            
            lambdaUpdate()
                    .setSql("random_num = random_num + 1")
                    .eq(WgRandom::getType, dictType)
                    .eq(WgRandom::getCreateDay, monthStr)
                    .update();
        } else {
            WgRandom newRandom = new WgRandom();
            newRandom.setType(dictType);
            newRandom.setCreateDay(monthStr);
            newRandom.setDictNoPrefix("A");
            newRandom.setRandomNum(1);
            newRandom.setDictName("发货需求");
            
            save(newRandom);
            distributorNo = "A" + monthStr + "0001";
        }
        return distributorNo;
    }
}
