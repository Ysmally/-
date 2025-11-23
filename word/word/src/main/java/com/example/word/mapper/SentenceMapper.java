package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.Sentence;
import org.apache.ibatis.annotations.Mapper;

/**
 * 句子表(sentence)的数据访问层
 */
@Mapper
public interface SentenceMapper extends BaseMapper<Sentence> {
}