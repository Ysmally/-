package com.example.word.repository;

import com.example.word.entity.Word;
import com.example.word.mapper.WordMapper;
import org.springframework.stereotype.Repository;

/**
 * 单词资源库
 * 这一层是可选的，主要用于封装更复杂的数据库交互逻辑。
 * 在当前项目中，大部分简单操作可以直接在Service层通过Mapper完成。
 * @Repository 注解将其标识为数据访问组件。
 */
@Repository
public class WordRepository {

    /**
     * 注入WordMapper以进行数据库操作
     * private final: 私有、不可变的成员变量，通过构造函数注入
     */
    private final WordMapper wordMapper;

    /**
     * 构造函数注入
     * @param wordMapper Spring容器自动注入的WordMapper实例
     */
    public WordRepository(WordMapper wordMapper) {
        this.wordMapper = wordMapper;
    }

    public Word findById(Integer id) {
        return wordMapper.selectById(id);
    }
}