package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.Word;
import org.apache.ibatis.annotations.Mapper;

/**
 * 单词表(word)的数据访问层
 */
@Mapper
public interface WordMapper extends BaseMapper<Word> {
}