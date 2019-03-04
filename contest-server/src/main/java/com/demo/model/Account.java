package com.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Account implements Serializable {


    private static final long serialVersionUID = -5833060742923499664L;
    private int id;
    private String name;
    private String username;
    private String password;
    private String qq;
    private String phone;
    private String email;
    private String description;
    private String avatarImgUrl;
    private int state;
    private int level;
    private Date createTime;
}
