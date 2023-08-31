package com.calcifer.weight.service;

import com.calcifer.weight.repository.RandomMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RandomService {

    @Autowired
    private RandomMapper randomMapper;

    /**
     * 随机数
     *
     * @return
     */
    public String randomUtils(String dictType, String monthStr) {
        Map<String, Object> params = new HashMap<String, Object>();
        String distributorNo = "";
        params.put("dictType", dictType);
        params.put("monthStr", monthStr);//当前年月
        List list = randomMapper.loadBySql(params);
        if (!list.isEmpty()) {
            Map map = (Map) list.get(0);
            String num = (MapUtils.getInteger(map, "dictNo") + 1) + "";
            String no = "";
            switch (num.length()) {
                case 1: {
                    no = "000" + num;
                }
                break;
                case 2: {
                    no = "00" + num;
                }
                break;
                case 3: {
                    no = "0" + num;
                }
                break;
                case 4: {
                    no = num;
                }
                break;
            }
            distributorNo = MapUtils.getString(map, "dictNoPrefix") + monthStr + no;//
            randomMapper.update(params);//需要修改一下
        } else {//需要增加一个
  			  /*
  			   * #{dictNo},
					#{dictName},
					#{dictNoPrefix},
					#{dictType},
					#{yearStr},
  			   */
            params.put("dictType", dictType);//类型
            params.put("yearStr", monthStr);//当前年份
            params.put("dictNoPrefix", "A");//费用报销
            params.put("dictNo", "1");//编号
            params.put("dictName", "发货需求");//名称
            randomMapper.add(params);//需要修改一下
            distributorNo = "A" + monthStr + "0001";
        }
        return distributorNo;
    }
}
