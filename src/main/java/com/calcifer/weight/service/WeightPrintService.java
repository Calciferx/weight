package com.calcifer.weight.service;

import com.calcifer.weight.config.ScaleConfigProperties;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeightPrintService {
    @Autowired
    private ScaleConfigProperties properties;
    @Autowired
    private CarService carService;

    public void print(WeightRecordDO weightRecordDO) throws Exception {
        print(weightRecordDO, null);
    }

    public void print(WeightRecordDO weightRecordDO, String templateKey) throws Exception {
        print(Collections.singletonList(weightRecordDO), templateKey);
    }

    public void print(List<WeightRecordDO> recordList, String templateKey) throws Exception {
        print(recordList, templateKey, null);
    }

    public void print(List<WeightRecordDO> recordList, String templateKey, Map<String, Object> parameters) throws Exception {
        JasperPrint jasperPrint = generateJasperPrint(recordList, templateKey, parameters);

        // 导出并打印
        File output = File.createTempFile("output", ".pdf");
        JasperExportManager.exportReportToPdfFile(jasperPrint, output.getAbsolutePath());
        try (BufferedInputStream pdfInputStream = FileUtil.getInputStream(output)) {
            PDFPrint(pdfInputStream);
        } finally {
            output.delete();
        }
    }

    public byte[] generatePdf(List<WeightRecordDO> recordList, String templateKey) throws Exception {
        return generatePdf(recordList, templateKey, null);
    }

    public byte[] generatePdf(List<WeightRecordDO> recordList, String templateKey, Map<String, Object> parameters) throws Exception {
        JasperPrint jasperPrint = generateJasperPrint(recordList, templateKey, parameters);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private JasperPrint generateJasperPrint(List<WeightRecordDO> recordList, String templateKey, Map<String, Object> parameters) throws Exception {
        String path = null;
        if (templateKey != null) {
            switch (templateKey) {
                case "standard":
                    if (recordList.size() > 1) {
                        throw new IllegalArgumentException("检斤单模板只支持单条记录打印");
                    }
                    path = properties.getStandardTemplate();
                    break;
                case "delivery":
                    if (recordList.size() > 1) {
                        throw new IllegalArgumentException("带出门证的检斤单模板只支持单条记录打印");
                    }
                    path = properties.getDeliveryTemplate();
                    break;
                case "waste":
                    path = properties.getWasteTemplate();
                    break;
                case "raw":
                    path = properties.getRawTemplate();
                    break;
                default:
                    throw new IllegalArgumentException("未知的模板类型: " + templateKey);
            }
        }

        if (path == null && templateKey == null) {
            // 默认逻辑：如果只有一条记录，尝试按物料ID区分
            String materialId = (recordList.size() == 1) ? recordList.get(0).getMaterialId().trim() : "";
            path = "DHJ".equals(materialId) ? properties.getStandardTemplate() : properties.getDeliveryTemplate();
        }

        if (path == null) {
            throw new IllegalArgumentException("未找到对应的打印模板配置，请检查配置文件");
        }

        log.info("Using print template: {}, records count: {}", path, recordList.size());

        // 数据映射转换层
        AtomicInteger seq = new AtomicInteger(1);
        List<Map<String, ?>> dataList = recordList.stream()
                .map(record -> {
                    Map<String, Object> m = convertToMap(record, templateKey, seq.getAndIncrement());
                    log.info("Mapped record: {}", m); // 打印每条记录映射后的详细内容
                    return m;
                })
                .collect(Collectors.toList());

        // 准备报表参数
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        prepareDefaultParameters(parameters, recordList);

        // 【关键修复】：为参数提供一个独立的数据源对象，防止指针冲突
        // 在 Jasper 模板的 Table 组件中，dataSourceExpression 应改为：$P{tableData}
        parameters.put("tableData", new JRMapCollectionDataSource(dataList));

        try (BufferedInputStream bis = FileUtil.getInputStream(path)) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bis);

            // 决定主报表的数据源
            JRDataSource mainDataSource;
            if ("waste".equals(templateKey) || "raw".equals(templateKey)) {
                // 对于汇总表格类模板，主报表只需运行一次（渲染一次 Title/Summary 里的 Table 即可）
                // 真正的多行数据已经通过 tableData 参数传进去了
                mainDataSource = new JREmptyDataSource(1);
            } else {
                // 对于普通的单据类模板，依然使用主数据源迭代
                mainDataSource = new JRMapCollectionDataSource(dataList);
            }

            return JasperFillManager.fillReport(jasperReport, parameters, mainDataSource);
        }

    }

    /**
     * 为多条记录报表准备默认统计参数
     */
    private void prepareDefaultParameters(Map<String, Object> parameters, List<WeightRecordDO> recordList) {
        if (recordList == null || recordList.isEmpty()) return;

        parameters.putIfAbsent("sheetId", DateUtil.format(recordList.get(0).getWeighDate(), "yyyyMMdd") + "001");

        // 计算总车辆数和总净重
        parameters.put("totalTruckNum", String.valueOf(recordList.size()));
        double totalNet = recordList.stream()
                .filter(r -> r.getNetWeight() != null)
                .mapToDouble(WeightRecordDO::getNetWeight)
                .sum();
        parameters.put("totalNetWeight", String.format("%.2f", totalNet));
    }

    private Map<String, Object> convertToMap(WeightRecordDO record, String templateKey, int seq) {
        Map<String, Object> map = new HashMap<>();
        // 1. 基础字段映射 (兼容驼峰和数据库字段名)
        map.put("id", record.getId());
        map.put("weighId", record.getWeighId());
        map.put("materialId", record.getMaterialId());
        map.put("supplierName", record.getSupplierName());
        map.put("materialName", record.getMaterialName());
        map.put("plateNumber", record.getPlateNumber());
        map.put("roughWeight", record.getRoughWeight());
        map.put("tareWeight", record.getTareWeight());
        map.put("netWeight", record.getNetWeight());
        map.put("weighDate", record.getWeighDate());
        map.put("comment", record.getComment());
        map.put("weighMan", record.getWeighMan());
        map.put("checkMan", record.getCheckMan());
        map.put("auditor", record.getAuditor());
        map.put("carType", record.getCarType());

        // 2. 日期格式化映射
        if (record.getWeighDate() != null) {
            map.put("weighDateStr", DateUtil.format(record.getWeighDate(), "yyyy-MM-dd HH:mm:ss"));
            map.put("weighDateShort", DateUtil.format(record.getWeighDate(), "yyyy-MM-dd"));
        }

        // 3. 针对特定模板的特殊映射 (此处可根据实际模板字段需求调整)
        if ("waste".equals(templateKey)) {
            // 代码
            map.put("code", "SW59");
            // 名称
            map.put("name", "一般工业固体废物");
            // 出厂时间
            map.put("time", DateUtil.format(record.getWeighDate(), "yyyy-MM-dd HH:mm"));
            // 出厂数量
            map.put("number", record.getNetWeight() != null ? record.getNetWeight().toString() : "0");
            // 出厂环节经办人
            map.put("manager", "");
            // 运输单位
            map.put("transUnit", properties.getWasteTransUnit());
            // 车牌号
            map.put("plateNumber", record.getPlateNumber());

            // 动态查询车辆信息（从 clxx 表）
            String driver = "";
            String phone = "";
            if (record.getPlateNumber() != null) {
                com.calcifer.weight.entity.domain.CarDO car = carService.getById(record.getPlateNumber());
                if (car != null) {
                    driver = car.getCarOwner() != null ? car.getCarOwner() : "";
                    phone = car.getPhone() != null ? car.getPhone() : "";
                }
            }
            map.put("driver", driver);
            map.put("phone", phone);

            // 运输方式
            map.put("transType", "公路");
            // 接收单位
            map.put("receiveUnit", properties.getWasteReceiveUnit());
            // 流向类型
            map.put("flowType", "省内转移");
        } else if ("raw".equals(templateKey)) {
            // 序号：String类型，1开始自增
            map.put("code", String.valueOf(seq));
            // 计量时间
            map.put("time", DateUtil.format(record.getWeighDate(), "yyyy-MM-dd HH:mm"));
            // 物料名称
            map.put("name", "固废");
            // 车牌号
            map.put("plateNumber", record.getPlateNumber());
            // 毛重
            map.put("roughWeight", record.getRoughWeight() != null ? record.getRoughWeight().toString() : "0");
            // 皮重
            map.put("tareWeight", record.getTareWeight() != null ? record.getTareWeight().toString() : "0");
            // 净重
            map.put("netWeight", record.getNetWeight() != null ? record.getNetWeight().toString() : "0");
            // 流向
            map.put("transOrientation", properties.getRawTransOrientation());
            // 检斤员
            map.put("weighMan", record.getWeighMan());
        }

        return map;
    }

    public static void PDFPrint(InputStream inputStream) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName("检斤单");
            //设置纸张及缩放
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            //设置多页打印
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //设置打印方向
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            // 添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    log.error("document close exception", e);
                }
            }
        }
    }

    public static Paper getPaper() {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        int width = 595;
        int height = 380;
        // 设置边距，单位是像素，10mm边距，对应 28px
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }


    public static void main(String[] args) throws Exception {
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        if (printServices.length == 0) {
            System.out.println("没有连接打印机");
            return;
        }
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.getPrintService() == null) {
            System.out.println("没有可用的打印机");
            return;
        }
        System.out.println(printerJob.getPrintService().getName());
        BufferedInputStream inputStream = FileUtil.getInputStream("C:\\Users\\Calcifer\\AppData\\Local\\Temp\\output7115266807229485883.pdf");
        PDFPrint(inputStream);
    }
}
