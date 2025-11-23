package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.Phrase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短语表(phrase)的数据访问层
 */
@Mapper
public interface PhraseMapper extends BaseMapper<Phrase> {
}