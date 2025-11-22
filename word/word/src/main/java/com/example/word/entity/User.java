package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    // 用户ID
    @TableId(value = "userID", type = IdType.INPUT)
    private String userID;

    // 用户名称
    private String userName;

    // 用户密码
    private String password;

    // 用户头像URL
    private String avator;

    // 用户所选词书
    private String targetBook;

    // 用户每组学习单词量
    private Integer needCount;
}