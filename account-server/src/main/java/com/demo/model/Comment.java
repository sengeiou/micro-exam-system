package com.demo.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Comment {

    private int id;
    private int userId;
    private int postId;
    private String content;
    private Date createTime;

    Account user;
    List<Reply> replies;
}
