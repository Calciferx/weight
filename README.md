# 智能地磅称重管理系统 (Weight Management System)

本项目是一个基于 Spring Boot 的工业级称重管理系统，集成了自动化过磅、实时监控、多格式报表打印及车辆信息管理等核心功能。

## 核心功能特性

### 1. 自动化过磅与实时反馈

* **WebSocket 实时通讯**：前端通过 WebSocket 订阅地磅状态、红外线传感器数据及实时重量，实现无刷新动态更新。
* **称重自动刷新**：称重完成后，后端通过 WebSocket 主动推送指令，前端自动重新加载称重记录表格（方案 B），确保数据即时性。
* **语音播报**：集成语音服务，对接称重流程进行实时操作指引。

### 2. 多格式打印与预览系统

* **JasperReports 集成**：采用 JasperReports 引擎，支持多种复杂的磅单和报表格式。
* **在线 PDF 预览**：支持在浏览器中直接预览生成的报表，实现“所见即所得”的打印体验。
* **全量报表打印**：支持根据前端当前的筛选条件（日期范围、完成状态等）一键生成包含多条记录的汇总报表。

### 3. 模板配置与校验机制

* **展平式配置**：模板路径在配置文件中直接定义，逻辑清晰，易于维护。
* **严格校验**：系统会自动判断模板适用范围，防止误操作：
    * **单记录专用**：检斤单、带出门证磅单（仅限 1 条记录）。
    * **多记录支持**：废弃物记录表、原始记录表（支持 1 条或多条记录）。
* **数据映射层**：内置数据转换转换逻辑，支持将后端 Bean 属性灵活映射到模板定义的 Field，支持日期格式化及业务常量注入。

## 技术堆栈

* **后端**：Spring Boot, MyBatis Plus, JasperReports, PDFBox, Hutool
* **前端**：Layui, jQuery, Bootstrap, Moment.js
* **数据库**：SQL Server (兼容其他主流数据库)

## 报表配置说明

### 配置文件项 (application.yaml)

```yaml
weight:
  standard-template: classpath:config/report.jasper       # 检斤单
  delivery-template: classpath:config/report2.jasper      # 带出门证的检斤单
  waste-template: classpath:config/report_waste.jasper    # 一般工业固体废弃物记录表
  raw-template: classpath:config/report_raw.jasper        # 检斤原始记录表
```

### 一般工业固体废弃物出厂环节记录表 (Waste Report) 字段映射

针对 `waste-template`，系统会自动关联 `clxx` (车辆信息表) 进行动态数据补全：

| 报表字段 (Field Name) | 数据来源         | 说明                  |
|:------------------|:-------------|:--------------------|
| `driver`          | `clxx.车主`    | 根据记录中的车牌号动态查询       |
| `phone`           | `clxx.电话`    | 根据记录中的车牌号动态查询       |
| `plateNumber`     | `qchjj.车牌号`  | 车辆识别号               |
| `time`            | `qchjj.检斤日期` | 格式：yyyy-MM-dd HH:mm |
| `number`          | `qchjj.净重`   | 自动转换为字符串显示          |
| `code`            | 常量           | 固定值：SW59            |
| `name`            | 常量           | 固定值：一般工业固体废物        |
| `transUnit`       | 配置项         | 可在 application.yaml 中配置    |
| `receiveUnit`     | 配置项         | 可在 application.yaml 中配置     |
| `transType`       | 常量           | 固定值：公路              |
| `flowType`        | 常量           | 固定值：省内转移            |
| `weighDateShort`  | `qchjj.检斤日期` | 格式：yyyy-MM-dd       |

## 操作指南

1. **自动过磅**：在 `chengzhong.html` 页面开启“自动过磅”开关。
2. **报表预览**：在下方记录表格选择“打印格式”，点击“报表预览”查看当前筛选结果的 PDF。
3. **报表打印**：点击“报表打印”将任务发送至服务器连接的默认打印机。
4. **单条补打**：在记录行右侧操作列，可针对单条记录进行特定格式的预览或打印。
