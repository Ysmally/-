package com.example.word.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.word.entity.MemoryRecord;
import com.example.word.mapper.MemoryRecordMapper;
import com.example.word.service.MemoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 记忆服务实现类
 * @author DaY1zz
 */
@Service
public class MemoryServiceImpl extends ServiceImpl<MemoryRecordMapper, MemoryRecord> implements MemoryService {

    @Override
    public List<MemoryRecord> getAllMemoryRecords() {
        return list();
    }

    @Override
    public void addMemoryRecord(MemoryRecord record) {
        // 如果没有设置上次复习时间，设置为当前时间
        if (record.getLastReviewTime() == null) {
            record.setLastReviewTime(LocalDateTime.now());
        }
        
        // 根据记忆等级设置下次复习时间
        if (record.getNextReviewTime() == null) {
            setNextReviewTime(record);
        }
        
        save(record);
    }

    @Override
    public void updateMemoryRecord(MemoryRecord record) {
        // 更新最后复习时间
        record.setLastReviewTime(LocalDateTime.now());
        
        // 根据记忆结果更新记忆等级和下次复习时间
        setNextReviewTime(record);
        
        updateById(record);
    }

    @Override
    public void deleteMemoryRecord(Long id) {
        removeById(id);
    }

    @Override
    public List<MemoryRecord> getRecordsToReview(Long userId) {
        QueryWrapper<MemoryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .le("next_review_time", LocalDateTime.now());
        return list(queryWrapper);
    }

    @Override
    public void updateMemoryResult(Long recordId, Boolean isRemembered) {
        MemoryRecord record = getById(recordId);
        if (record == null) {
            return;
        }
        
        record.setIsRemembered(isRemembered);
        record.setLastReviewTime(LocalDateTime.now());
        
        // 根据记忆结果调整记忆等级
        if (isRemembered) {
            record.setMemoryLevel(record.getMemoryLevel() + 1);
        } else {
            // 忘记了，降低记忆等级
            record.setMemoryLevel(Math.max(1, record.getMemoryLevel() - 1));
        }
        
        // 增加复习次数
        record.setReviewTimes(record.getReviewTimes() + 1);
        
        // 设置下次复习时间
        setNextReviewTime(record);
        
        updateById(record);
    }

    @Override
    public MemoryRecord getOrCreateMemoryRecord(Long userId, Long wordId) {
        QueryWrapper<MemoryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("word_id", wordId);
        MemoryRecord record = getOne(queryWrapper);
        
        if (record == null) {
            record = new MemoryRecord();
            record.setUserId(userId);
            record.setWordId(wordId);
            record.setMemoryLevel(1);
            record.setReviewTimes(0);
            record.setIsRemembered(false);
            record.setLastReviewTime(LocalDateTime.now());
            setNextReviewTime(record);
            save(record);
            
            // 重新获取以确保ID已设置
            record = getOne(queryWrapper);
        }
        
        return record;
    }
    
    /**
     * 根据记忆等级设置下次复习时间
     * @param record 记忆记录
     */
    private void setNextReviewTime(MemoryRecord record) {
        LocalDateTime now = LocalDateTime.now();
        int level = record.getMemoryLevel() != null ? record.getMemoryLevel() : 1;
        
        // 根据记忆等级设置复习间隔
        switch (level) {
            case 1:
                record.setNextReviewTime(now.plusMinutes(5));  // 5分钟后
                break;
            case 2:
                record.setNextReviewTime(now.plusHours(2));    // 2小时后
                break;
            case 3:
                record.setNextReviewTime(now.plusDays(1));     // 1天后
                break;
            case 4:
                record.setNextReviewTime(now.plusDays(3));     // 3天后
                break;
            case 5:
                record.setNextReviewTime(now.plusWeeks(1));    // 1周后
                break;
            case 6:
                record.setNextReviewTime(now.plusWeeks(2));    // 2周后
                break;
            default:
                record.setNextReviewTime(now.plusMonths(1));  // 1个月后（6级以上）
                break;
        }
    }
}