package com.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Post {
    private int id;
    private int authorId;
    private String htmlContent;
    private String textContent;
    private Date createTime;
    private Date updateTime;
    private Date lastReplyTime;
    private int replyNum;
    private String title;
    private Account author;
}
