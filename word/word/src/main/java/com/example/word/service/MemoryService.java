// src/main/java/com/example/word/service/MemoryService.java
package com.example.word.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.word.common.Result;
import com.example.word.entity.DateRecord;
import com.example.word.entity.MemoryRecord;
import com.example.word.mapper.DateRecordMapper;
import com.example.word.mapper.MemoryRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemoryService extends ServiceImpl<MemoryRecordMapper, MemoryRecord> {

    @Autowired
    private DateRecordMapper dateRecordMapper;

    /** 获取今日需复习单词 */
    public List<MemoryRecord> getReviewList(String userID) {
        QueryWrapper<MemoryRecord> qw = new QueryWrapper<>();
        qw.eq("userID", userID)
                .le("nextReviewTime", LocalDate.now());
        return this.list(qw);
    }

    /** 提交学习/复习结果 */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> studyWord(String userID, Integer wordID, boolean isKnow) {
        QueryWrapper<MemoryRecord> qw = new QueryWrapper<>();
        qw.eq("userID", userID).eq("wordID", wordID);
        MemoryRecord record = this.getOne(qw);

        boolean isNewWord = (record == null);

        if (isNewWord) {
            record = new MemoryRecord();
            record.setUserID(userID);
            record.setWordID(wordID);
            record.setPhase(0);
            // 新词默认明天复习
            record.setNextReviewTime(LocalDate.now().plusDays(1));
        }

        if (isKnow) {
            int nextDays = getNextInterval(record.getPhase());
            record.setPhase(record.getPhase() + 1);
            record.setNextReviewTime(LocalDate.now().plusDays(nextDays));
        } else {
            record.setPhase(0);
            record.setNextReviewTime(LocalDate.now().plusDays(1));
        }

        this.saveOrUpdate(record);

        // 更新每日统计
        updateDailyRecord(userID, !isNewWord);

        return Result.success("打卡成功");
    }

    private int getNextInterval(int phase) {
        return switch (phase) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 7;
            case 4 -> 15;
            default -> 30;
        };
    }

    private void updateDailyRecord(String userID, boolean isReview) {
        LocalDate today = LocalDate.now();
        QueryWrapper<DateRecord> qw = new QueryWrapper<>();
        qw.eq("userID", userID).eq("date", today);
        DateRecord dateRecord = dateRecordMapper.selectOne(qw);

        if (dateRecord == null) {
            dateRecord = new DateRecord();
            dateRecord.setUserID(userID);
            dateRecord.setDate(today);
            dateRecord.setLearnCount(0);
            dateRecord.setReviewCount(0);
            dateRecord.setLearningTime(0);
        }

        if (isReview) {
            dateRecord.setReviewCount(dateRecord.getReviewCount() + 1);
        } else {
            dateRecord.setLearnCount(dateRecord.getLearnCount() + 1);
        }
        dateRecord.setLearningTime(dateRecord.getLearningTime() + 1);

        if (dateRecord.getId() == null) {
            dateRecordMapper.insert(dateRecord);
        } else {
            dateRecordMapper.updateById(dateRecord);  // 推荐用 updateById，更简单
        }
    }
}