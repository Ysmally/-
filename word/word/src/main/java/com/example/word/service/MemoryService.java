package com.example.word.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.word.entity.MemoryRecord;

import java.util.List;

/**
 * 记忆服务接口
 * @author DaY1zz
 */
public interface MemoryService extends IService<MemoryRecord> {

    List<MemoryRecord> getAllMemoryRecords();

    void addMemoryRecord(MemoryRecord record);

    void updateMemoryRecord(MemoryRecord record);

    void deleteMemoryRecord(Long id);

    List<MemoryRecord> getRecordsToReview(Long userId);

    void updateMemoryResult(Long recordId, Boolean isRemembered);

    MemoryRecord getOrCreateMemoryRecord(Long userId, Long wordId);
}