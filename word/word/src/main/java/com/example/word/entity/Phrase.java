package com.example.word.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 短语实体
 * @author DaY1zz
 */
@Data
@TableName("phrase")
public class Phrase {
    @TableId(type = IdType.AUTO)
    private Integer phraseID;
    private Integer wordID;
    private String enPhrase;
    private String cnPhrase;
}