package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 翻译实体
 * @author DaY1zz
 */
@Data
@TableName("translation")
public class Translation {
    @TableId(type = IdType.AUTO)
    private Integer translationID;
    private Integer wordID;
    private String enTran;
    private String cnTran;
    private String wordType;
}