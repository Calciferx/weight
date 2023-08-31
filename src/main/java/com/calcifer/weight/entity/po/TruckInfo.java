package com.calcifer.weight.entity.po;

import com.xiaoleilu.hutool.date.DateTime;
import lombok.Data;

@Data
public class TruckInfo {
    private String ID;
    private String cardNum;
    private String carNum;
    private double tare;
    private String faHuo;
    private String shouHuo;
    private String goods;
    private String spec;
    private double bundle;
    private double scale;
    private double price;
    private String memo;
    private String backup1;
    private String backup2;
    private String backup3;
    private String backup4;
    private String backup5;
    private String backup10;
    private String backup11;
    private String backup12;
    private String backup13;
    private String backup14;
    private int type;
    private DateTime StartTime;
    private DateTime EndTime;
}
