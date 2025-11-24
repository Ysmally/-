package com.example.word.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.word.entity.Phrase;
import com.example.word.entity.Sentence;
import com.example.word.entity.Translation;
import com.example.word.entity.Word;
import com.example.word.mapper.PhraseMapper;
import com.example.word.mapper.SentenceMapper;
import com.example.word.mapper.TranslationMapper;
import com.example.word.mapper.WordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单词服务实现类
 */
@Service
public class WordService extends ServiceImpl<WordMapper, Word> {

    /**
     * 注入关联表的Mapper
     * @Autowired: Spring自动注入依赖
     * private: 私有成员变量
     */
    @Autowired
    private TranslationMapper translationMapper;
    @Autowired
    private PhraseMapper phraseMapper;
    @Autowired
    private SentenceMapper sentenceMapper;

    /**
     * 获取单词的完整详情，包括单词本身、所有翻译、所有短语和所有例句
     * @param wordID 单词的ID
     * @return 一个Map，包含了单词的所有相关信息；如果单词不存在，返回null
     */
    public Map<String, Object> getWordDetail(Integer wordID) {
        Word word = this.getById(wordID);
        if (word == null) {
            return null; // 单词不存在
        }

        // 查询关联翻译
        QueryWrapper<Translation> tranQw = new QueryWrapper<>();
        tranQw.eq("wordID", wordID);
        List<Translation> translations = translationMapper.selectList(tranQw);

        // 查询关联短语
        QueryWrapper<Phrase> phraseQw = new QueryWrapper<>();
        phraseQw.eq("wordID", wordID);
        List<Phrase> phrases = phraseMapper.selectList(phraseQw);

        // 查询关联例句
        QueryWrapper<Sentence> sentQw = new QueryWrapper<>();
        sentQw.eq("wordID", wordID);
        List<Sentence> sentences = sentenceMapper.selectList(sentQw);

        // 组装返回结果
        Map<String, Object> detail = new HashMap<>();
        detail.put("word", word);
        detail.put("translations", translations);
        detail.put("phrases", phrases);
        detail.put("sentences", sentences);

        return detail;
    }

    /**
     * 根据书名获取该书的所有单词列表
     * （分页逻辑通常在Controller层配合MyBatisPlus的Page对象处理）
     * @param bookName 书本名称
     * @return 单词列表
     */
    public List<Word> getWordsByBook(String bookName) {
        QueryWrapper<Word> qw = new QueryWrapper<>();
        qw.eq("belong_book", bookName);
        return this.list(qw);
    }
}