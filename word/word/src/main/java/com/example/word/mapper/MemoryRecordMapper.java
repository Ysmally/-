// 文件路径: src/main/java/com/example/word/mapper/MmryRecordMapper.java
package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.MmryRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记忆记录表(memory_record)的数据访问层
 */
@Mapper
public interface MemoryRecordMapper extends BaseMapper<MmryRecord> {
}