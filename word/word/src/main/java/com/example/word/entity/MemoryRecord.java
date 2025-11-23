package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.MemroyRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记忆记录表(memory_record)的数据访问层
 */
@Mapper
public interface MemoryRecordMapper extends BaseMapper<MmryRecord> {
}