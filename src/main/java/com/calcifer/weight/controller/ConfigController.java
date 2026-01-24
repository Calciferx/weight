package com.calcifer.weight.controller;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("config")
@Tag(name = "配置管理", description = "JSON配置文件管理")
public class ConfigController {

    private final String CONFIG_DIR = System.getProperty("user.dir") + File.separator + "config" + File.separator;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("list")
    @Operation(summary = "获取配置文件列表", description = "获取所有JSON配置文件列表")
    public RespWrapper<List<String>> list() {
        try (Stream<Path> paths = Files.list(Paths.get(CONFIG_DIR))) {
            List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".json"))
                    .collect(Collectors.toList());
            return new RespWrapper<>(files);
        } catch (IOException e) {
            log.error("Error listing config files", e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "无法读取配置目录: " + e.getMessage());
        }
    }

    @GetMapping("get")
    @Operation(summary = "获取配置文件内容", description = "获取指定配置文件的JSON内容")
    public RespWrapper<Object> get(@RequestParam String filename) {
        Path path = Paths.get(CONFIG_DIR + filename);
        if (!Files.exists(path)) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "文件不存在");
        }
        try {
            String content = Files.readString(path);
            Object json = objectMapper.readValue(content, Object.class);
            return new RespWrapper<>(json);
        } catch (IOException e) {
            log.error("Error reading config file", e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "读取文件失败: " + e.getMessage());
        }
    }

    @PostMapping("save")
    @Operation(summary = "保存配置文件", description = "保存或更新配置文件")
    public RespWrapper<?> save(@RequestBody Map<String, Object> payload) {
        String filename = (String) payload.get("filename");
        Object content = payload.get("content");

        if (filename == null || !filename.endsWith(".json")) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "文件名无效");
        }

        try {
            Path path = Paths.get(CONFIG_DIR + filename);
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(content);
            Files.writeString(path, jsonString);
            return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        } catch (IOException e) {
            log.error("Error writing config file", e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "写入文件失败: " + e.getMessage());
        }
    }

    @PostMapping("delete")
    @Operation(summary = "删除配置文件", description = "删除指定的配置文件")
    public RespWrapper<?> delete(@RequestBody Map<String, String> payload) {
        String filename = payload.get("filename");
        if (filename == null) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "文件名不能为空");
        }
        try {
            Path path = Paths.get(CONFIG_DIR + filename);
            if (Files.deleteIfExists(path)) {
                return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
            } else {
                return new RespWrapper<>(false, RespCodeEnum.FAILED, "文件不存在");
            }
        } catch (IOException e) {
            log.error("Error deleting config file", e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "删除失败: " + e.getMessage());
        }
    }
}
