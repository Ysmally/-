package com.example.word.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.word.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表(user)的数据访问层
 * @Mapper 注解表明这是一个MyBatis的Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper<User> 已经提供了对User实体类的增、删、改、查等常用方法
    // 无需手动编写SQL
}