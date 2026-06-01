package com.calcifer.weight.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.repository.WeightRecordMapper;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class WeightRecordService extends ServiceImpl<WeightRecordMapper, WeightRecordDO> implements IService<WeightRecordDO> {

    private final ConcurrentHashMap<String, AtomicLong> idCounters = new ConcurrentHashMap<>();

    public String generateWeighId(String materialCode) {
        String dateStr = DateUtil.format(new Date(), "yyMM");
        String key = materialCode + dateStr;

        AtomicLong counter = idCounters.computeIfAbsent(key, k -> {
            List<WeightRecordDO> recordDOList = this.lambdaQuery()
                    .select(WeightRecordDO::getWeighId)
                    .likeRight(WeightRecordDO::getWeighId, materialCode + dateStr)
                    .list();

            if (recordDOList.isEmpty()) {
                return new AtomicLong(0);
            } else {
                String prefix = materialCode + dateStr;
                long maxNum = 0;

                for (WeightRecordDO record : recordDOList) {
                    String weighId = record.getWeighId().trim();
                    if (weighId.startsWith(prefix)) {
                        String seqStr = weighId.substring(prefix.length()).replaceAll("[^0-9]", "");
                        if (!seqStr.isEmpty()) {
                            try {
                                long currentNum = Long.parseLong(seqStr);
                                if (currentNum > maxNum) {
                                    maxNum = currentNum;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
                return new AtomicLong(maxNum);
            }
        });

        long nextNum = counter.incrementAndGet();
        return materialCode + dateStr + String.format("%05d", nextNum);
    }

}
