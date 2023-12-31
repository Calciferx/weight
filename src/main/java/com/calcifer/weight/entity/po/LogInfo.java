package com.calcifer.weight.entity.po;

import lombok.Data;

/**
 * 操作日志(sys_log_info)
 *
 * @author bianj
 * @version 1.0.0 2020-12-11
 */
@Data
public class LogInfo implements java.io.Serializable {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -6844394301470348717L;

    /* This code was generated by TableGo tools, mark 1 begin. */

    /**
     * id
     */
    private String id;

    /**
     * 类型
     */
    private String type;

    /**
     * 模块
     */
    private String modular;

    /**
     * 功能
     */
    private String functionLog;

    /**
     * 操作
     */
    private String operationLog;

    /**
     * 用户
     */
    private String nameLog;

    /**
     * 机器名称
     */
    private String ipLog;

    /**
     * 单据日期
     */
    private String orderDate;

    /**
     * 操作时间
     */
    private String createTime;

    /**
     * 单据编码
     */
    private String code;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 0.平台 1.配送方 2.采购方
     */
    private String logType;

    /**
     * 采购商/配送商ID
     */
    private String businessId;

    /* This code was generated by TableGo tools, mark 1 end. */

    public LogInfo() {
    }

    public LogInfo(String id, String type, String modular, String functionLog, String operationLog, String nameLog, String ipLog, String orderDate, String createTime, String code, String content, String logType, String businessId) {
        this.id = id;
        this.type = type;
        this.modular = modular;
        this.functionLog = functionLog;
        this.operationLog = operationLog;
        this.nameLog = nameLog;
        this.ipLog = ipLog;
        this.orderDate = orderDate;
        this.createTime = createTime;
        this.code = code;
        this.content = content;
        this.logType = logType;
        this.businessId = businessId;
    }
}