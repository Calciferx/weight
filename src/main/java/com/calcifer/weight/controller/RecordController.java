package com.calcifer.weight.controller;

import com.calcifer.weight.entity.dto.RecordDTO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.RecordPO;
import com.calcifer.weight.entity.vo.RecordVO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("record")
@Tag(name = "称重记录", description = "称重记录")
public class RecordController {
    @Autowired
    private RecordService recordService;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @PostMapping(value = "add")
    @Operation(summary = "增加称重记录", description = "增加称重记录")
    public RespWrapper<List<RecordVO>> add(@RequestBody RecordDTO recordDTO) {

        RecordPO recordPO = new RecordPO();
        List<RecordVO> recordList = recordService.getRecordList(recordPO);
        return new RespWrapper<>(recordList);
    }

    @PostMapping(value = "query")
    @Operation(summary = "查询称重记录", description = "查询称重记录")
    public RespWrapper<List<RecordVO>> query(@RequestBody RecordDTO recordDTO) {

        RecordPO recordPO = new RecordPO();
        List<RecordVO> recordList = recordService.getRecordList(recordPO);
        return new RespWrapper<>(recordList);
    }

    @PostMapping(value = "update")
    @Operation(summary = "修改称重记录", description = "修改称重记录")
    public RespWrapper<?> update(@RequestBody RecordDTO recordDTO) {
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }


    @PostMapping(value = "delete")
    @Operation(summary = "删除称重记录", description = "删除称重记录")
    public RespWrapper<?> delete(@RequestBody RecordDTO recordDTO) {
        return new RespWrapper<>(RespCodeEnum.SUCCESS);
    }
}