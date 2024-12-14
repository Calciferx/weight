package com.calcifer.weight.service;

import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

@Slf4j
@Service
public class WeightPrintService {
    @Value("${calcifer.weight.print-template}")
    private String printTemplatePath;
    @Value("${calcifer.weight.print-template2}")
    private String printTemplatePath2;

    public void print(WeightRecordDO weightRecordDO) throws Exception {
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        if (printServices.length == 0) {
            log.error("没有连接打印机");
            return;
        }
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.getPrintService() == null) {
            log.error("没有找到默认打印机");
            return;
        }
        // 加载报表模板
        BufferedInputStream bis = FileUtil.getInputStream("DHJ".equals(weightRecordDO.getMaterialId().trim()) ? printTemplatePath : printTemplatePath2);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bis);

        List<WeightRecordDO> recordDOList = Collections.singletonList(weightRecordDO);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(recordDOList);
        // 填充报表数据
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

        // 导出报表
        File output = File.createTempFile("output", ".pdf");
        log.info("print PDF: {}", output.getAbsolutePath());
        JasperExportManager.exportReportToPdfFile(jasperPrint, output.getAbsolutePath());
        BufferedInputStream pdfInputStream = FileUtil.getInputStream(output);
        PDFPrint(pdfInputStream);

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
