package com.calcifer.weight.repository;

import com.calcifer.weight.entity.dto.ReportInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMapper {
    List<ReportInfo> getReportInfoByGroup(ReportInfo reportInfo);

    List<ReportInfo> getReportInfo(ReportInfo reportInfo);
}
