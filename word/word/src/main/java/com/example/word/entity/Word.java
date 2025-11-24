package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * 单词实体
 * @author DaY1zz
 */
@Data
@TableName("word")
public class Word {
    @TableId(type = IdType.AUTO)
    private Integer wordID;
    private String word;
    private String ukPhone;
    private String usPhone;
    private String remMethod;
    private String picUrl;
    private String belongBook;
}