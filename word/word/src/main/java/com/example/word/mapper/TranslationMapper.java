package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.Translation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 翻译表(translation)的数据访问层
 */
@Mapper
public interface TranslationMapper extends BaseMapper<Translation> {
}