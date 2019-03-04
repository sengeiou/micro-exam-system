package com.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Reply {

    private int id;
    private int userId;
    private int atuserId;
    private int postId;
    private int commentId;
    private String content;
    private Date createTime;

    private Account user;
    private Account atuser;
}
