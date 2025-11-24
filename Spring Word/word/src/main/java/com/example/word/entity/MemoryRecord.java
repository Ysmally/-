package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("memory_record")
public class MemoryRecord {

    private Integer wordID;        // 联合主键之一
    private String userID;         // 联合主键之一

    private Integer phase;         // 记忆阶段

    private LocalDate nextReviewTime;   // ← 关键：字段名必须是这个！
}