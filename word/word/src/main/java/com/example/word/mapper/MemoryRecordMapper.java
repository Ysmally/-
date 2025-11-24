package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.MemoryRecord; // [MODIFIED] 导入新的实体类
import org.apache.ibatis.annotations.Mapper;

/**
 * 记忆记录表(memory_record)的数据访问层
 */
@Mapper
public interface MemoryRecordMapper extends BaseMapper<MemoryRecord> { // [MODIFIED] 接口名和泛型已更改
}