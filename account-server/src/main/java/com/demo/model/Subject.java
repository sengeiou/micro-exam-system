package com.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Subject {

    private int id;
    private String name;
    private Date createTime;
    private Date updateTime;
    private int questionNum;
    private String imgUrl;
    private int state;
}
