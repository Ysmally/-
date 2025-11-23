package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("date_record")
public class DateRecord {
    private String userID;
    private LocalDate date;
    private Integer learnCount;
    private Integer reviewCount;
    private Integer learningTime;
}