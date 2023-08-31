package com.calcifer.weight.controller;


import com.calcifer.weight.entity.enums.ActionEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.po.LogInfo;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.LogService;
import com.calcifer.weight.utils.DateUtil;
import com.calcifer.weight.utils.ExportExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志管理
 */
@RequestMapping("/sys/logs")
@RestController
@Api(basePath = "/sys/logs", value = "系统 操作日志", description = "系统", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;


    /**
     * 描述: 列表
     */
    @PostMapping(value = "/findAll.do")
    @ApiOperation(value = "列表", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Object> listAll(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = false, value = "查询时间") @RequestParam(required = false) String createTime,
            @ApiParam(required = false, value = "查询时间") @RequestParam(required = false) String webSocketName,
            @ApiParam(required = true, value = "1.查询2.导出") @RequestParam(required = true) ActionEnum flag,
//			@ApiParam(required = false, value = "页码", defaultValue = "1") @RequestParam(required = false) Integer pageNum,
//            @ApiParam(required = false, value = "每页条数", defaultValue = "10") @RequestParam(required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();

        LogInfo logInfo = new LogInfo();
        logInfo.setCreateTime(createTime);
        List<LogInfo> logInfoList = logService.findSysLogInfoByCondition(logInfo);
        if (flag != ActionEnum.QUERY) {
            List<Map<Object, Object>> logInfoMap = logInfoList.stream().map(BeanMap::new).collect(Collectors.toList());
//                    List list = customerService.findAllList(params, ".pageList");
            String folderPath = request.getSession().getServletContext().getRealPath("/upload/user/");
            // String fileName = DateUtil.getSdfTimess() + "操作日志_导出.xls";
            String fileName = DateUtil.getSdfTimess() + "OperationLog.xls";
            String url = "/upload/user/";
            String[][] columnNames = {{"用户", "ip", "模块", "功能", "操作时间", "操作内容"},
                    {"name", "ip", "modular", "function", "createTime", "operation"}};
            String[] columnWidth = {"14", "25", "16", "17", "15", "17"};
            ExportExcelUtil.exportExcel("操作日志", folderPath, columnNames, columnWidth, logInfoMap, fileName);
            webSocketHandler.sendMessageToUser(webSocketName, new WSRespWrapper<>("/upload/user/" + fileName, WSCodeEnum.exportMsg));
            return new RespWrapper<>(url + fileName, RespCodeEnum.SUCCESS);
        } else {
            return new RespWrapper<>(logInfoList, RespCodeEnum.SUCCESS);
        }
    }
}
