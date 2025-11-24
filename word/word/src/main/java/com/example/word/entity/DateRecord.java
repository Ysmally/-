package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("date_record")
public class DateRecord {

    @TableId(type = IdType.AUTO)   // ← 加上这一行！！
    private Long id;               // ← 加上这个字段！！

    private String userID;
    private LocalDate date;

    private Integer learnCount;
    private Integer reviewCount;
    private Integer learningTime;
}