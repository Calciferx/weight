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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/adminx/report")
@Api(basePath = "/adminx/report", value = "统计称重信息", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @ResponseBody
    @RequestMapping(value = "/findList.do", method = RequestMethod.POST)
    @ApiOperation(value = "统计", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Object> findList(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = false, value = "统计类型（1.货名2.车号）") @RequestParam(required = false) String staticsType,
            @ApiParam(required = false, value = "过磅模式") @RequestParam(required = false) String weighingMode,
            @ApiParam(required = false, value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(required = false, value = "结束时间") @RequestParam(required = false) String endTime,
            @ApiParam(required = true, value = "1.查询2.导出") @RequestParam(required = true) ActionEnum flag,
            @ApiParam(required = false, value = "websocket名称") @RequestParam(required = false) String webSocketName,
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
            return new RespWrapper<>(url + fileName, RespCodeEnum.SUCCESS);
        } else {
            reportInfos = reportService.getReportInfo(queryReportInfo);
            return new RespWrapper<>(reportInfos, RespCodeEnum.SUCCESS);
        }
    }

    //TODO 统一异常处理
    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
