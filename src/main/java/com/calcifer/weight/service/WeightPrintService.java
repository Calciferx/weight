package com.calcifer.weight.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import java.awt.print.PrinterJob;

@Slf4j
@Service
public class WeightPrintService {
    public static void main(String[] args) {
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
    }
}
