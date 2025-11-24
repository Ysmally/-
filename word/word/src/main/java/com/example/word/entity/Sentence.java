package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 句子实体
 * @author DaY1zz
 */
@Data
@TableName("sentence")
public class Sentence {
    @TableId(type = IdType.AUTO)
    private Integer sentenceID;
    private Integer wordID;
    private String enSentence;
    private String cnSentence;
}