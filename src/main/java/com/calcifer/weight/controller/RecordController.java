package com.calcifer.weight.controller;

import com.calcifer.weight.entity.domain.RecordPO;
import com.calcifer.weight.entity.dto.RecordDto;
import com.calcifer.weight.entity.enums.*;
import com.calcifer.weight.entity.vo.RecordVO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.RecordService;
import com.calcifer.weight.utils.DateUtil;
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

import static com.calcifer.weight.common.WeightContext.TOKEN_USER_MAP;

@Slf4j
@RestController
@RequestMapping("/adminx/record")
public class RecordController {

    @Autowired
    private RecordService recordService;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    /**
     * @return 记录列表
     */
    @PostMapping(value = "/pageList.do")
    public RespWrapper<List<RecordVO>> pageList(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = false, value = "时间类型（1.本日记录2.本周记录3.本月记录4.本季纪录5.本年记录）") @RequestParam(required = false) RecordTypeEnum recordType,
            @ApiParam(required = false, value = "完成情况类型(1.未完成记录2.已完成记录)") @RequestParam(required = false) CompleteStatusEnum logType) {

        if (!TOKEN_USER_MAP.containsKey(token)) {
            return new RespWrapper<>(RespCodeEnum.IS_NOT_LOGIN_ERROR);
        }
        RecordPO recordPO = new RecordPO();
        if (recordType != null) {
            switch (recordType) {
                case CURRENT_DAY:
                    recordPO.setCreateTime(DateUtil.getDay());
                    break;
                case CURRENT_WEEK:
                    recordPO.setStartTime(DateUtil.getTimesWeekmorning());
                    recordPO.setEndTime(DateUtil.getTimesWeeknight());
                    break;
                case CURRENT_MONTH:
                    recordPO.setCreateTime(DateUtil.getSdfYearMonth_Str());
                    break;
                case CURRENT_QUARTER:
                    recordPO.setStartTime(DateUtil.getCurrentQuarterStartTime());
                    recordPO.setEndTime(DateUtil.getCurrentQuarterEndTime());
                    break;
                case CURRENT_YEAR:
                    recordPO.setCreateTime(DateUtil.getYear());
                    break;
            }
        }
        if (logType == CompleteStatusEnum.COMPLETED) {
            recordPO.setTareNull1(logType);
        } else {
            recordPO.setTareNull(logType);
        }
        List<RecordVO> recordList = recordService.getRecordList(recordPO);
        return new RespWrapper<>(recordList);
    }

    /**
     * 查询称重信息
     **/
    @PostMapping(value = "/findList.do")
    @ApiOperation(value = "查询称重信息", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Object> pageList(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "1.查询2.导出") @RequestParam(required = true) ActionEnum flag,
            @ApiParam(required = false, value = "webSocketName") @RequestParam(required = false) String webSocketName,
            @ApiParam(required = false, value = "车号(包含)") @RequestParam(required = false) String carNum,
            @ApiParam(required = false, value = "车号(等于)") @RequestParam(required = false) String carNo,
            @ApiParam(required = false, value = "货名") @RequestParam(required = false) String goodsName,
            @ApiParam(required = false, value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(required = false, value = "结束时间") @RequestParam(required = false) String endTime,
            @ApiParam(required = false, value = "完成情况类型(1.未完成记录2.已完成记录)") @RequestParam(required = false) CompleteStatusEnum logType,
            @ApiParam(required = false, value = "过磅模式") @RequestParam(required = false) String weighingMode,
            HttpServletRequest request) {
        if (!TOKEN_USER_MAP.containsKey(token)) {
            return new RespWrapper<>(RespCodeEnum.IS_NOT_LOGIN_ERROR);
        }
        RecordDto queryRecordDto = new RecordDto(carNum, goodsName, carNo, startTime, endTime, weighingMode);
        if (logType != null) {
            if (logType == CompleteStatusEnum.COMPLETED) {
                queryRecordDto.setTareNull1(logType);
            } else {
                queryRecordDto.setTareNull(logType);
            }
        }
        List<RecordDto> resultList = recordService.findRecordList(queryRecordDto);
        if (flag == ActionEnum.QUERY) {
            return new RespWrapper<>(resultList);
        } else {
            String folderPath = request.getSession().getServletContext().getRealPath("/upload/record/");
            String fileName = DateUtil.getSdfTimess() + "record.xls";
            String url = "/upload/record/";
            String[][] columnNames = {
                    {"流水号", "车号", "发货单位", "收货单位", "货名", "规格", "毛重", "皮重", "净重", "扣重", "实重", "单价", "金额", "折方系数", "方量", "过磅费", "毛重司磅员", "皮重司磅员", "毛重时间", "皮重时间", "备注"},
                    {"serialNumber", "carNum", "shipper", "receivingUnit", "goodsName", "model", "grossWeight", "tareWeight", "netWeight", "deductionWeight", "realWeight", "price", "money", "factor", "squareAmount", "weightFee", "grossWeightman", "tareWeightman", "grossWeightTime", "tareWeightTime", "remark"}
            };
            String[] columnWidth = {"20", "20", "25", "20", "15", "15", "15", "15", "15", "15", "15", "15", "15", "15", "15", "15", "15", "15", "25", "25", "20"};
            List<Map<Object, Object>> mapList = resultList.stream().map(BeanMap::new).collect(Collectors.toList());
//            result.put("api", "/adminx/record/findList.do");
            webSocketHandler.sendMessageToUser(webSocketName, new WSRespWrapper<>(url + fileName, WSCodeEnum.exportMsg));

            return new RespWrapper<>(url + fileName, RespCodeEnum.SUCCESS);
        }
    }


    /**
     * 删除称重信息
     **/
    @PostMapping(value = "/delete.do")
    @ApiOperation(value = "删除称重信息", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Object> deleteSlave(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            @ApiParam(required = true, value = "流水号(英文逗号分隔)") @RequestParam(required = true) String serialNumbers,
            HttpServletRequest request
    ) {
        if (!TOKEN_USER_MAP.containsKey(token)) {
            return new RespWrapper<>(RespCodeEnum.IS_NOT_LOGIN_ERROR);
        }
        int deletedNum = recordService.deleteRecordByIds(serialNumbers.split(","));
        if (deletedNum > 0) {
            return new RespWrapper<>(RespCodeEnum.SUCCESS);
        }
        return new RespWrapper<>(RespCodeEnum.ERROR);
    }

    //TODO 统一异常处理
    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
