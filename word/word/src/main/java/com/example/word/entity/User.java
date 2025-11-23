package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId
    private String userID;
    private String userName;
    private String password;
    private String avator;
    private String targetBook;
    private Integer needCount;
}