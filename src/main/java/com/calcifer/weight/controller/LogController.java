package com.calcifer.weight.controller;


import com.calcifer.weight.entity.enums.ActionEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.po.LogInfo;
import com.calcifer.weight.entity.vo.PageWrapper;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.LogService;
import com.calcifer.weight.utils.DateUtil;
import com.calcifer.weight.utils.ExportExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("log")
@RestController
@Tag(name = "操作记录", description = "操作记录")
public class LogController {
    @Autowired
    private LogService logService;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;


    @PostMapping(value = "/query")
    @Operation(summary = "查询操作记录", description = "查询操作记录")
    public RespWrapper<?> query(){
        return null;
    }

    @PostMapping(value = "/delete")
    @Operation(summary = "删除操作记录", description = "删除操作记录")
    public RespWrapper<?> delete(){
        return null;
    }
}