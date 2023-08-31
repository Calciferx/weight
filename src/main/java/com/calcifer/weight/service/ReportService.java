package com.calcifer.weight.service;

import com.calcifer.weight.entity.dto.ReportInfo;
import com.calcifer.weight.repository.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportMapper reportMapper;

    public List<ReportInfo> getReportInfoByGroup(ReportInfo reportInfo) {
        return reportMapper.getReportInfoByGroup(reportInfo);
    }

    public List<ReportInfo> getReportInfo(ReportInfo reportInfo) {
        return reportMapper.getReportInfo(reportInfo);
    }
}
