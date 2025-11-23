package com.example.word;

import com.example.word.entity.MemoryRecord;
import com.example.word.service.MemoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 记忆功能测试
 * @author DaY1zz
 */
@SpringBootTest
@ActiveProfiles("test")
public class MemoryTest {

    @Autowired
    private MemoryService memoryService;

    @Test
    public void testAddMemoryRecord() {
        MemoryRecord record = new MemoryRecord();
        record.setUserId(1L);
        record.setWordId(1L);
        record.setMemoryLevel(1);
        record.setReviewTimes(0);
        record.setLastReviewTime(LocalDateTime.now());
        record.setNextReviewTime(LocalDateTime.now().plusDays(1));
        record.setIsRemembered(false);
        
        memoryService.addMemoryRecord(record);
        
        assertNotNull(record.getId());
        assertTrue(record.getId() > 0);
    }

    @Test
    public void testGetAllMemoryRecords() {
        List<MemoryRecord> records = memoryService.getAllMemoryRecords();
        assertNotNull(records);
        assertTrue(records.size() >= 0);
    }

    @Test
    public void testUpdateMemoryRecord() {
        MemoryRecord record = new MemoryRecord();
        record.setUserId(1L);
        record.setWordId(2L);
        record.setMemoryLevel(1);
        record.setReviewTimes(0);
        record.setIsRemembered(false);
        
        memoryService.addMemoryRecord(record);
        
        record.setMemoryLevel(2);
        record.setReviewTimes(1);
        record.setIsRemembered(true);
        
        memoryService.updateMemoryRecord(record);
        
        MemoryRecord updated = memoryService.getById(record.getId());
        assertEquals(2, updated.getMemoryLevel());
        assertEquals(1, updated.getReviewTimes());
        assertTrue(updated.getIsRemembered());
    }

    @Test
    public void testDeleteMemoryRecord() {
        MemoryRecord record = new MemoryRecord();
        record.setUserId(1L);
        record.setWordId(3L);
        record.setMemoryLevel(1);
        record.setIsRemembered(false);
        
        memoryService.addMemoryRecord(record);
        Long id = record.getId();
        
        memoryService.deleteMemoryRecord(id);
        
        assertNull(memoryService.getById(id));
    }

    @Test
    public void testGetRecordsToReview() {
        List<MemoryRecord> records = memoryService.getRecordsToReview(1L);
        assertNotNull(records);
        // 可以根据实际数据调整断言
    }
}