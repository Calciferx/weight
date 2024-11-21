package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.repository.WeightRecordMapper;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WeightRecordService extends ServiceImpl<WeightRecordMapper, WeightRecordDO> implements IService<WeightRecordDO> {
    public String generateWeighId(String materialCode) {
        WeightRecordDO recordDO = this.lambdaQuery()
                .likeRight(WeightRecordDO::getWeighId, materialCode)
                .apply("DATEDIFF(DAY, [检斤日期], GETDATE()) = 0")
                .orderByDesc(WeightRecordDO::getWeighId)
                .one();
        String dateStr = DateUtil.format(new Date(), "yyMM");
        if (recordDO == null) {
            return materialCode + dateStr + "001";
        } else {
            String numStr = recordDO.getWeighId().trim().replaceAll("[a-zA-Z]", "").substring(4);
            int num = Integer.parseInt(numStr);
            String incNumStr = String.format("%03d", num + 1);
            return materialCode + dateStr + incNumStr;
        }
    }

}
