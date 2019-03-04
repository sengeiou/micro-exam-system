package com.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Contest {

    private int id;
    private String title;
    private int totalScore;
    private int subjectId;
    private Date createTime;
    private Date updateTime;
    private Date startTime;
    private Date endTime;
    private int state;
    private String subjectName;

}
