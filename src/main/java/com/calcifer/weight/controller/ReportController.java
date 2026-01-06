package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.ReportInfo;
import com.calcifer.weight.entity.enums.ActionEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.ReportService;
import com.calcifer.weight.utils.DateUtil;
import com.calcifer.weight.utils.ExportExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/adminx/report")
@Tag(name = "统计称重信息")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @ResponseBody
    @RequestMapping(value = "/findList.do", method = RequestMethod.POST)
    @Operation(summary = "统计", description = "条件：无")
    public RespWrapper<Object> findList(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "统计类型（1.货名2.车号）") @RequestParam(required = false) String staticsType,
            @Parameter(description = "过磅模式") @RequestParam(required = false) String weighingMode,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "1.查询2.导出", required = true) @RequestParam(required = true) ActionEnum flag,
            @Parameter(description = "websocket名称") @RequestParam(required = false) String webSocketName,
            HttpServletRequest request) {

        ReportInfo queryReportInfo = new ReportInfo(staticsType, startTime, endTime, weighingMode);
        List<ReportInfo> reportInfos = reportService.getReportInfoByGroup(queryReportInfo);
        if (flag != ActionEnum.QUERY) {
            String folderPath = request.getSession().getServletContext().getRealPath("/upload/weight/");
            String fileName = DateUtil.getSdfTimess() + "称重统计信息.xls";
            String url = "/upload/weight/";
            String[][] columnNames = null;
            String[] columnWidth = null;
            if (("1").equals(staticsType)) {
                columnNames = new String[][]{{"货名", "毛重", "皮重", "净重", "小计车次"},
                        {"goodsName", "roughWeight", "tareWeight", "netWeight", "carNum"}};
                columnWidth = new String[]{"14", "25", "16", "17", "15", "17"};
            } else {
                columnNames = new String[][]{{"车号", "毛重", "皮重", "净重", "小计车次"},
                        {"carNo", "roughWeight", "tareWeight", "netWeight", "carNum"}};
                columnWidth = new String[]{"14", "25", "16", "17", "15"};
            }
            List<Map<Object, Object>> mapList = reportInfos.stream().map(BeanMap::new).collect(Collectors.toList());
            int exportResult = ExportExcelUtil.exportExcel("称重统计信息", folderPath, columnNames, columnWidth, mapList, fileName);
//            result.put("api", "/adminx/report/findList.do");
            webSocketHandler.sendMessageToUser(webSocketName, new WSRespWrapper<>(url + fileName, WSCodeEnum.exportMsg));
            return new RespWrapper<>(url + fileName);
        } else {
            reportInfos = reportService.getReportInfo(queryReportInfo);
            return new RespWrapper<>(reportInfos);
        }
    }

    //TODO 统一异常处理
    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.EXCEPTION);
    }
}