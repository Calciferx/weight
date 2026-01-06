package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSONObject;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.LogInfo;
import com.calcifer.weight.entity.po.SlaveInfo;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.LogService;
import com.calcifer.weight.service.SlaveDetailService;
import com.calcifer.weight.service.SlaveInfoService;
import com.calcifer.weight.utils.DateUtil;
import com.calcifer.weight.utils.IpUtil;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.calcifer.weight.common.WeightContext.TOKEN_USER_MAP;

@Slf4j
@RestController
@RequestMapping("/adminx/slave")
@Tag(name = "从机档案")
public class SlaveController {
    @Autowired
    private SlaveInfoService slaveInfoService;
    @Autowired
    private SlaveDetailService slaveDetailService;
    @Autowired
    private LogService logService;
    @Autowired
    private DeviceService deviceService;
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    /**
     * 新增从机档案
     */
    @PostMapping(value = "/addSlave.do")
    @Operation(summary = "新增", description = "条件：无")
    public RespWrapper<Boolean> addSlave(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "从机IP地址", required = true) @RequestParam(required = true) String slaveIp,
            @Parameter(description = "从机名称") @RequestParam(required = false) String slaveName,
            @Parameter(description = "从机编码") @RequestParam(required = false) String slaveCode,
            @Parameter(description = "线圈名称（只读属性）", required = true) @RequestParam(required = true) String coilName,
            @Parameter(description = "线圈数量", required = true) @RequestParam(required = true) int coilNum,
            @Parameter(description = "寄存器名称(可读可写)", required = true) @RequestParam(required = true) String discreteName,
            @Parameter(description = "寄存器数量", required = true) @RequestParam(required = true) int discreteNum,
            @Parameter(description = "备注") @RequestParam(required = false) String remark,
            @Parameter(description = "状态（1.启用2.停用）", required = true) @RequestParam(required = true) String status,
            HttpServletRequest request
    ) {
        if (slaveInfoService.count(slaveIp, null, null) > 0) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "ip已存在");
        }
        SlaveInfo slaveInfo = new SlaveInfo(UUID.randomUUID().toString(), slaveIp, slaveName, slaveCode, coilName, coilNum, discreteName, discreteNum, DateUtil.getTime(), remark, status);
        int flag = slaveInfoService.add(slaveInfo);
        if (flag < 1) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "操作失败");
        }
        SlaveDetailInfo slaveDetailInfo = new SlaveDetailInfo(DateUtil.getTime(), "", slaveInfo.getId(), "2");
        for (int i = 0; i < coilNum; i++) {
            slaveDetailInfo.setId(UUID.randomUUID().toString());
            slaveDetailInfo.setSerialName(coilName + i);
            slaveDetailInfo.setSerialSort(i);
            slaveDetailService.addDetails(slaveDetailInfo);
        }
        for (int i = 0; i < discreteNum; i++) {
            slaveDetailInfo.setId(UUID.randomUUID().toString());
            slaveDetailInfo.setSerialName(discreteName + i);
            slaveDetailInfo.setSerialSort(i);
            slaveDetailService.addDetails(slaveDetailInfo);
        }
        LogInfo logInfo = new LogInfo(null, null, "基础档案", "从机档案", "新增", TOKEN_USER_MAP.get(token).getRealName(), IpUtil.getClientIpAddress(request), null, null, slaveInfo.getId(), "新增" + slaveIp + "从机档案", null, null);
        logService.addLog(logInfo);
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
    }


    /**
     * 修改从机档案
     */
    @PostMapping(value = "/updateSlave.do")
    @Operation(summary = "修改", description = "条件：无")
    public RespWrapper<Boolean> updateSlave(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "从机Id", required = true) @RequestParam(required = true) String id,
            @Parameter(description = "从机IP地址") @RequestParam(required = false) String slaveIp,
            @Parameter(description = "从机名称") @RequestParam(required = false) String slaveName,
            @Parameter(description = "从机编码") @RequestParam(required = false) String slaveCode,
            @Parameter(description = "线圈名称（只读属性）") @RequestParam(required = false) String coilName,
            @Parameter(description = "线圈数量") @RequestParam(required = false) Integer coilNum,
            @Parameter(description = "寄存器名称(可读可写)") @RequestParam(required = false) String discreteName,
            @Parameter(description = "寄存器数量") @RequestParam(required = false) Integer discreteNum,
            @Parameter(description = "备注") @RequestParam(required = false) String remark,
            @Parameter(description = "状态（1.启用2.停用）") @RequestParam(required = false) String status,
            @Parameter(description = "从机串口字符串") @RequestParam(required = false) String jsonStr,
            HttpServletRequest request
    ) {
        SlaveInfo slaveInfo = new SlaveInfo(id, slaveIp, slaveName, slaveCode, coilName, coilNum, discreteName, discreteNum, null, remark, status);
        if (slaveInfoService.count(slaveIp, null, null) > 0) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "ip已存在");
        }
        Integer flag = slaveInfoService.update(slaveInfo);
        if (flag < 1) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
        if (StringUtils.hasLength(jsonStr)) {
            List<SlaveDetailInfo> slaveDetailInfos = JSONObject.parseArray(jsonStr, SlaveDetailInfo.class);
            Integer delete = slaveDetailService.delete(id);
            if (slaveDetailInfos != null && !slaveDetailInfos.isEmpty()) {
                for (SlaveDetailInfo slaveDetailInfo : slaveDetailInfos) {
                    slaveDetailInfo.setId(UUID.randomUUID().toString());
                    slaveDetailInfo.setUpdateTime(DateUtil.getTime());
                    slaveDetailInfo.setSlaveId(id);
                    slaveDetailService.addDetails(slaveDetailInfo);
                }
            } else {
                SlaveDetailInfo slaveDetailInfo = new SlaveDetailInfo(DateUtil.getTime(), "", slaveInfo.getId(), "2");
                for (int i = 0; i < coilNum; i++) {
                    slaveDetailInfo.setId(UUID.randomUUID().toString());
                    slaveDetailInfo.setSerialName(coilName + i);
                    slaveDetailInfo.setSerialSort(i);
                    slaveDetailService.addDetails(slaveDetailInfo);
                }
                for (int i = 0; i < discreteNum; i++) {
                    slaveDetailInfo.setId(UUID.randomUUID().toString());
                    slaveDetailInfo.setSerialName(discreteName + i);
                    slaveDetailInfo.setSerialSort(i);
                    slaveDetailService.addDetails(slaveDetailInfo);
                }
            }
        }
        LogInfo logInfo = new LogInfo(null, null, "基础档案", "从机档案", "修改", TOKEN_USER_MAP.get(token).getRealName(), IpUtil.getClientIpAddress(request), null, null, slaveInfo.getId(), "修改" + slaveIp + "从机档案", null, null);
        logService.addLog(logInfo);
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
    }

    /**
     * 删除从机档案
     */
    @PostMapping(value = "/deleteSlave.do")
    @Operation(summary = "删除", description = "条件：无")
    public RespWrapper<Boolean> deleteSlave(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "从机Id", required = true) @RequestParam(required = true) String id,
            @Parameter(description = "ip") @RequestParam(required = false) String slaveIp,
            HttpServletRequest request
    ) {
        Integer count = slaveInfoService.count(null, id, "2");
        if (count < 1) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "请停用后重试");
        }
        int flag = slaveInfoService.delete(id);
        Integer deletedNum = slaveDetailService.delete(id);
        if ((flag & deletedNum) != 0) {
            LogInfo logInfo = new LogInfo(null, null, "基础档案", "从机档案", "删除", TOKEN_USER_MAP.get(token).getRealName(), IpUtil.getClientIpAddress(request), null, null, id, "删除" + slaveIp + "从机档案", null, null);
            logService.addLog(logInfo);
        } else {
            return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS, "操作成功");
    }

    @PostMapping(value = "/pageList.do")
    @Operation(summary = "列表（分页）", description = "条件：无")
    public RespWrapper<List<SlaveInfo>> pageList(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "模糊查询字段") @RequestParam(required = false) String keywords,
            @Parameter(description = "状态(1.启用2.停用)") @RequestParam(required = false) String status,
            @Parameter(description = "页码", required = true) @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", required = true) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
//        PageInfo<SlaveInfo> pageInfo = PageHelper.startPage(pageNum, pageNum).doSelectPageInfo(() -> slaveInfoService.querySlaveInfo(null, null, status, keywords));
//        if (pageInfo != null) {
//            return new RespWrapper<>(pageInfo.getList(), RespCodeEnum.SUCCESS, (int) pageInfo.getTotal(), pageInfo.getPages(), "操作成功");
//        } else {
//            return new RespWrapper<>(null, RespCodeEnum.SUCCESS, "操作成功");
//        }
        return null;
    }


    @PostMapping(value = "/findAll.do")
    @Operation(summary = "查询全部", description = "条件：无")
    public RespWrapper<List<SlaveInfo>> findAll(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "模糊查询字段") @RequestParam(required = false) String keywords,
            @Parameter(description = "状态(1.启用2.停用)") @RequestParam(required = false) String status,
            HttpServletRequest request) {
        List<SlaveInfo> slaveInfos = slaveInfoService.querySlaveInfo(null, null, status, keywords);
        return new RespWrapper<>(slaveInfos);
    }

    /**
     * 修改设备状态
     */
    @PostMapping(value = "/updateStatus.do")
    @Operation(summary = "修改设备状态", description = "条件：无")
    public RespWrapper<Boolean> updateStatus(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "从机IP地址") @RequestParam(required = false) String slaveIp,
            @Parameter(description = "设备排序") @RequestParam(required = false) Integer sort,
            @Parameter(description = "状态（true.开启false.关闭）") @RequestParam(required = false) boolean status,
            //TODO boolean下面还判断？
            @Parameter(description = "关联设备类型（1.进端红外2.进端道闸3.进端红绿灯4.出端红外线5.出端道闸6.出端红绿灯7.进端红外2 8.进端红外2 9.进端道闸关 10、出端道闸关）") @RequestParam(required = false) boolean type,
            HttpServletRequest request
    ) {
        deviceService.controlModBusDevice(sort, status);
        return new RespWrapper<Boolean>(true, RespCodeEnum.SUCCESS, "操作成功");
    }


    @PostMapping(value = "/findDetail.do")
    @Operation(summary = "查询详情", description = "条件：无")
    public RespWrapper<List<SlaveDetailInfo>> findDetail(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            @Parameter(description = "主表id", required = true) @RequestParam(required = true) String slaveId,
            HttpServletRequest request) {
        List<SlaveDetailInfo> slaveDetailInfos = slaveDetailService.querySlaveDetailInfo(slaveId, null, null);
        return new RespWrapper<>(slaveDetailInfos);
    }

    @PostMapping(value = "/initSystem.do")
    @Operation(summary = "初始化系统", description = "条件：无")
    public RespWrapper<Boolean> initSystem(
            @Parameter(description = "token", required = true) @RequestParam(required = true) String token,
            HttpServletRequest request) throws ModbusIOException {
        log.info("init system...send reset event");
        log.info("now state machine status is {}", weighStateMachine.getState());
        weighStateMachine.sendEvent(WeighEventEnum.RESET);
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.EXCEPTION);
    }
}