package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.WeightPrintService;
import com.calcifer.weight.service.WeightRecordService;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("admin/weightRecord")
public class WeightRecordController {
    @Autowired
    private WeightRecordService weightRecordService;
    @Autowired
    private WeightPrintService weightPrintService;

    @RequestMapping("query")
    public RespWrapper<List<WeightRecordDO>> getWeightInfo(@RequestParam("startTime") String startTimeStr, @RequestParam("endTime") String endTimeStr, CompleteStatusEnum completeStatus) {
        DateTime startTime = DateUtil.parse(startTimeStr);
        DateTime endTime = DateUtil.parse(endTimeStr);
        List<WeightRecordDO> list = weightRecordService.lambdaQuery()
                .between(WeightRecordDO::getWeighDate, startTime, endTime)
                .isNull(completeStatus == CompleteStatusEnum.UNCOMPLETED, WeightRecordDO::getNetWeight)
                .isNotNull(completeStatus == CompleteStatusEnum.COMPLETED, WeightRecordDO::getNetWeight)
                .list();
        return new RespWrapper<>(list, RespCodeEnum.SUCCESS);
    }

    @GetMapping("preview")
    public void preview(@RequestParam("id") Long id, @RequestParam(value = "templateKey", required = false) String templateKey, HttpServletResponse response) throws Exception {
        WeightRecordDO recordDO = weightRecordService.lambdaQuery().eq(WeightRecordDO::getId, id).one();
        if (recordDO == null) {
            return;
        }
        byte[] pdfBytes = weightPrintService.generatePdf(Collections.singletonList(recordDO), templateKey);
        writePdfResponse(response, pdfBytes);
    }

    @GetMapping("previewReport")
    public void previewReport(@RequestParam("startTime") String startTimeStr,
                              @RequestParam("endTime") String endTimeStr,
                              @RequestParam(value = "completeStatus", required = false) CompleteStatusEnum completeStatus,
                              @RequestParam(value = "templateKey", required = false) String templateKey,
                              HttpServletResponse response) throws Exception {
        try {
            List<WeightRecordDO> list = queryRecords(startTimeStr, endTimeStr, completeStatus);
            byte[] pdfBytes = weightPrintService.generatePdf(list, templateKey);
            writePdfResponse(response, pdfBytes);
        } catch (IllegalArgumentException e) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('" + e.getMessage() + "');window.close();</script>");
        }
    }

    @PostMapping("print")
    public RespWrapper<String> print(@RequestParam("id") Long id, @RequestParam(value = "templateKey", required = false) String templateKey) {
        try {
            WeightRecordDO recordDO = weightRecordService.lambdaQuery().eq(WeightRecordDO::getId, id).one();
            if (recordDO == null) {
                return new RespWrapper<>(RespCodeEnum.ERROR, "记录不存在");
            }
            weightPrintService.print(Collections.singletonList(recordDO), templateKey);
            return new RespWrapper<>(RespCodeEnum.SUCCESS, "指令已发送至打印机");
        } catch (Exception e) {
            log.error("print error", e);
            return new RespWrapper<>(RespCodeEnum.ERROR, e.getMessage());
        }
    }

    @PostMapping("printReport")
    public RespWrapper<String> printReport(@RequestParam("startTime") String startTimeStr,
                                           @RequestParam("endTime") String endTimeStr,
                                           @RequestParam(value = "completeStatus", required = false) CompleteStatusEnum completeStatus,
                                           @RequestParam(value = "templateKey", required = false) String templateKey) {
        try {
            List<WeightRecordDO> list = queryRecords(startTimeStr, endTimeStr, completeStatus);
            weightPrintService.print(list, templateKey);
            return new RespWrapper<>(RespCodeEnum.SUCCESS, "报表打印指令已发送");
        } catch (Exception e) {
            log.error("printReport error", e);
            return new RespWrapper<>(RespCodeEnum.ERROR, e.getMessage());
        }
    }

    private List<WeightRecordDO> queryRecords(String startTimeStr, String endTimeStr, CompleteStatusEnum completeStatus) {
        DateTime startTime = DateUtil.parse(startTimeStr);
        DateTime endTime = DateUtil.parse(endTimeStr);
        return weightRecordService.lambdaQuery()
                .between(WeightRecordDO::getWeighDate, startTime, endTime)
                .isNull(completeStatus == CompleteStatusEnum.UNCOMPLETED, WeightRecordDO::getNetWeight)
                .isNotNull(completeStatus == CompleteStatusEnum.COMPLETED, WeightRecordDO::getNetWeight)
                .list();
    }

    private void writePdfResponse(HttpServletResponse response, byte[] pdfBytes) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=preview.pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @RequestMapping("saveOrUpdate")
    public RespWrapper<Boolean> saveOrUpdate(@RequestBody WeightRecordDO weightRecordDO) {
        boolean result = weightRecordService.saveOrUpdate(weightRecordDO);
        if (result) return new RespWrapper<>(RespCodeEnum.SUCCESS);
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    @RequestMapping("delete")
    public RespWrapper<Boolean> delete(@RequestBody WeightRecordDO weightRecordDO) {
        boolean result = weightRecordService.remove(new LambdaQueryWrapper<WeightRecordDO>().eq(WeightRecordDO::getId, weightRecordDO.getId()));
        if (result) return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "删除成功");
        return new RespWrapper<>(null, RespCodeEnum.ERROR, "删除失败");
    }
}
