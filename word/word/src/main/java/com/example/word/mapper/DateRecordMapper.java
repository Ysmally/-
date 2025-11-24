package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.DateRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日期记录表(date_record)的数据访问层
 */
@Mapper
public interface DateRecordMapper extends BaseMapper<DateRecord> {
}