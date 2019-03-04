package com.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Question {

    private int id;
    private String title;
    private String content;
    private int questionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String parse;
    private int subjectId;
    private int contestId;
    private int score;
    private int difficulty;
    private Date createTime;
    private Date updateTime;
    private int state;

    private String subjectName;

}
