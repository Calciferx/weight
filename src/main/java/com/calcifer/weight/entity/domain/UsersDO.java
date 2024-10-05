package com.calcifer.weight.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
* 
* @TableName users
*/
@Data
@TableName("users")
public class UsersDO extends Model<UsersDO> {

    private static final long serialVersionUID = 5414908831016737745L;


    @NotNull(message="[]不能为空")
    @TableId(value = "序号", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message="[]不能为空")
    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("用户名")
    private String username;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("密码")
    private String password;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("用户类型")
    private String type;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("所在部门")
    private String department;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("真实姓名")
    private String realName;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("用户管理")
    private String canManageUser;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("权限分配")
    private String canDistributeAuth;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("供应商管理")
    private String canManageSupplier;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("汽车信息登记")
    private String canAssignCarInfo;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("检斤单录入与检斤")
    private String canWeigh;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("打印检斤单")
    private String canPrintInvoice;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("直接化验分析")
    private String directlyTestAndAnalyse;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("检斤物料分析")
    private String weighMaterialAnalyse;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("石油焦扣水")
    private String petroleumCokeDeductWater;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("火车信息登记")
    private String trainInfoAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物料信息登记")
    private String materialInfoAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("成分信息登记")
    private String componentInfoAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物料成分维护")
    private String materialComponentMaintain;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("取样送样登记")
    private String sampleExtractAndDeliverAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("检斤相关信息查询")
    private String weighInfoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("化验分析信息查询")
    private String testAndAnalyseInfoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物配相关信息查询")
    private String materialTransInfoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("其他基础信息查询")
    private String otherBasicInfoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("系统管理")
    private String systemManage;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物料检斤")
    private String materialWeigh;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("化验分析")
    private String testAndAnalyse;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物配信息")
    private String materialTransInfo;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("信息查询")
    private String infoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("物料分析查询")
    private String materialAnalyseQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("质量数量报告查询")
    private String qualityAndQuantityReportQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("石油焦扣扣水查询")
    private String petroleumCokeDeductWaterQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("火车检斤录入")
    private String trainWeighInput;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("火车取送样登记")
    private String trainSampleExtractAndDeliverAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("用户登录信息查询")
    private String userLoginInfoQuery;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("汽车取送样登记")
    private String carSampleExtractAndDeliverAssign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("分析结果签发")
    private String analyseResultSign;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("化验分析复样")
    private String testAndAnalyseDuplicateSample;

    @Size(max= 10,message="编码长度不能超过10")
    @Length(max= 10,message="编码长度不能超过10")
    @TableField("复样抽查")
    private String duplicateSampleSpotCheck;

}
