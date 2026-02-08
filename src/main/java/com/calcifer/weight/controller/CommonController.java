package com.calcifer.weight.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.po.*;
import com.calcifer.weight.entity.vo.PageWrapper;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("common/{module}")
@Tag(name = "通用接口", description = "通用CRUD接口")
public class CommonController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, Class<?>> entityMap = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends IService>> serviceMap = new ConcurrentHashMap<>();

    public CommonController() {
        // Init Entity Mapping
        entityMap.put("user", UserPO.class);
        entityMap.put("record", RecordPO.class);
        entityMap.put("card", CardInfoPO.class);
        entityMap.put("vehicle", CardInfoPO.class);
        entityMap.put("autoweigh", RecordPO.class);
        entityMap.put("slaveInfo", SlaveInfoPO.class);
        entityMap.put("slaveDetail", SlaveDetailPO.class);

        // Init Service Mapping
        serviceMap.put("user", UserService.class);
        serviceMap.put("record", RecordService.class);
        serviceMap.put("card", CardService.class);
        serviceMap.put("vehicle", CardService.class);
        serviceMap.put("autoweigh", RecordService.class);
        serviceMap.put("slaveInfo", SlaveInfoService.class);
        serviceMap.put("slaveDetail", SlaveDetailService.class);
    }

    private IService getService(String module) {
        Class<? extends IService> serviceClass = serviceMap.get(module);
        if (serviceClass == null) {
            throw new IllegalArgumentException("Unknown module: " + module);
        }
        return applicationContext.getBean(serviceClass);
    }

    private Class<?> getEntityClass(String module) {
        Class<?> clazz = entityMap.get(module);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown module: " + module);
        }
        return clazz;
    }

    @GetMapping("columns")
    @Operation(summary = "获取表列定义", description = "从配置文件获取表列定义")
    public RespWrapper<?> getColumns(@PathVariable String module) {
        try {
            String configDir = System.getProperty("user.dir") + File.separator + "config" + File.separator;
            File file = new File(configDir + module + "_columns.json");
            
            if (!file.exists()) {
                // Fallback: Try to load from classpath if external file not found (optional, but good for safety)
                Resource resource = applicationContext.getResource("classpath:config/" + module + "_columns.json");
                if (resource.exists()) {
                     try (InputStream is = resource.getInputStream()) {
                        List<Map<String, Object>> columns = objectMapper.readValue(is, List.class);
                        return new RespWrapper<>(columns);
                    }
                }
                return new RespWrapper<>(false, RespCodeEnum.FAILED, "Column definition not found for module: " + module);
            }
            
            List<Map<String, Object>> columns = objectMapper.readValue(file, List.class);
            return new RespWrapper<>(columns);
        } catch (Exception e) {
            log.error("Error reading column definition", e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, e.getMessage());
        }
    }

    @PostMapping("add")
    @Operation(summary = "通用新增", description = "通用新增接口")
    public RespWrapper<?> add(@PathVariable String module, @RequestBody Map<String, Object> body) {
        IService service = getService(module);
        Class<?> entityClass = getEntityClass(module);
        Object entity = objectMapper.convertValue(body, entityClass);
        boolean result = service.save(entity);
        if (result) return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        else return new RespWrapper<>(false, RespCodeEnum.FAILED);
    }

    @PostMapping("delete")
    @Operation(summary = "通用删除", description = "通用删除接口")
    public RespWrapper<?> delete(@PathVariable String module, @RequestBody Map<String, Object> body) {
        IService service = getService(module);
        Class<?> entityClass = getEntityClass(module);
        
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        String keyProperty = tableInfo != null ? tableInfo.getKeyProperty() : null;
        
        Object idVal = null;
        if (keyProperty != null && body.containsKey(keyProperty)) {
             idVal = body.get(keyProperty);
        } else {
             if (body.containsKey("id")) idVal = body.get("id");
             else if (body.containsKey("userId")) idVal = body.get("userId");
             else if (body.containsKey("serialNum")) idVal = body.get("serialNum");
             else if (body.containsKey("ID")) idVal = body.get("ID");
        }

        if (idVal != null) {
            log.warn("Delete ID value: {}, Type: {}", idVal, idVal.getClass().getName());
            boolean result;
            if (idVal instanceof List) {
                // 使用 QueryWrapper 进行批量删除，避免 removeByIds 可能的参数绑定问题
                QueryWrapper<Object> wrapper = new QueryWrapper<>();
                // 确保使用数据库列名
                String keyColumn = tableInfo.getKeyColumn();
                wrapper.in(keyColumn, (List<?>) idVal);
                result = service.remove(wrapper);
            } else {
                result = service.removeById((Serializable) idVal);
            }
            
            if (result) return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
            else return new RespWrapper<>(false, RespCodeEnum.FAILED);
        }
        
        return new RespWrapper<>(false, RespCodeEnum.FAILED, "Cannot determine ID for deletion");
    }

    @PostMapping("update")
    @Operation(summary = "通用修改", description = "通用修改接口")
    public RespWrapper<?> update(@PathVariable String module, @RequestBody Map<String, Object> body) {
        IService service = getService(module);
        Class<?> entityClass = getEntityClass(module);
        Object entity = objectMapper.convertValue(body, entityClass);
        boolean result = service.updateById(entity);
        if (result) return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
        else return new RespWrapper<>(false, RespCodeEnum.FAILED);
    }

    @PostMapping("query")
    @Operation(summary = "通用查询", description = "通用查询接口")
    public RespWrapper<PageWrapper<?>> query(@PathVariable String module, @RequestBody Map<String, Object> body) {
        IService service = getService(module);
        Class<?> entityClass = getEntityClass(module);
        
        int current = 1;
        int pageSize = 10;
        if (body.containsKey("current")) current = Integer.parseInt(body.get("current").toString());
        if (body.containsKey("pageSize")) pageSize = Integer.parseInt(body.get("pageSize").toString());
        
        Page page = new Page<>(current, pageSize);
        QueryWrapper wrapper = new QueryWrapper<>();
        
        // 获取表信息用于字段名转换
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 跳过分页参数和操作符参数
            if ("current".equals(key) || "pageSize".equals(key) || "module".equals(key) || key.endsWith("_op")) {
                continue;
            }
            
            // 获取对应的数据库列名
            String columnName = getColumnName(tableInfo, key);
            if (columnName == null) {
                // 如果找不到对应的列名，可能是前端传了一些非数据库字段，忽略
                continue;
            }
            
            // 获取操作符，默认为 eq
            String op = (String) body.get(key + "_op");
            if (op == null) op = "eq";
            
            // 处理空值，如果值为空且不是范围查询，通常忽略（或者根据业务需求处理）
            if (value == null || value.toString().isEmpty()) {
                continue;
            }

            switch (op) {
                case "eq":
                    wrapper.eq(columnName, value);
                    break;
                case "like":
                    wrapper.like(columnName, value);
                    break;
                case "gt":
                    wrapper.gt(columnName, value);
                    break;
                case "ge":
                    wrapper.ge(columnName, value);
                    break;
                case "lt":
                    wrapper.lt(columnName, value);
                    break;
                case "le":
                    wrapper.le(columnName, value);
                    break;
                case "between":
                    // 假设前端传来的 value 是一个 List 或数组，或者逗号分隔的字符串
                    // ProTable 的 dateRange 通常传两个字段 key[]，或者在 transform 后变成 key_start, key_end
                    // 如果前端通过 SearchFormItem 传值，可能会传一个数组
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        if (list.size() >= 2) {
                            wrapper.between(columnName, list.get(0), list.get(1));
                        }
                    } else if (value.toString().contains(",")) {
                        String[] split = value.toString().split(",");
                        if (split.length >= 2) {
                            wrapper.between(columnName, split[0], split[1]);
                        }
                    }
                    break;
                default:
                    wrapper.eq(columnName, value);
            }
        }
        
        IPage resultPage = service.page(page, wrapper);
        
        return new RespWrapper<>(new PageWrapper<>(resultPage.getRecords(), (int)resultPage.getCurrent(), (int)resultPage.getSize(), (int)resultPage.getTotal()));
    }

    private String getColumnName(TableInfo tableInfo, String propertyName) {
        if (tableInfo == null) return null;
        
        // 检查主键
        if (propertyName.equals(tableInfo.getKeyProperty())) {
            return tableInfo.getKeyColumn();
        }
        
        // 检查普通字段
        for (com.baomidou.mybatisplus.core.metadata.TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            if (propertyName.equals(fieldInfo.getProperty())) {
                return fieldInfo.getColumn();
            }
            // 兼容一下可能传入已经是下划线的情况（虽然不推荐）
            if (propertyName.equals(fieldInfo.getColumn())) {
                return fieldInfo.getColumn();
            }
        }
        return null;
    }
}