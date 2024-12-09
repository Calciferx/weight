package com.calcifer.weight.entity.dto;

import lombok.Data;

@Data
public class MenuItem {
    private int sortno;
    private String name;
    private String icon;
    private String pid;
    private String id;
    private String url;
    private int auth;
}
