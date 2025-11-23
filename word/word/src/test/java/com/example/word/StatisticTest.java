package com.example.word;

import com.example.word.common.StatisticData;
import com.example.word.service.StatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计功能测试
 * @author DaY1zz
 */
@SpringBootTest
@ActiveProfiles("test")
public class StatisticTest {

    @Autowired
    private StatisticService statisticService;

    @Test
    public void testGetStatisticOverview() {
        StatisticData data = statisticService.getStatisticOverview();
        
        assertNotNull(data);
        assertNotNull(data.getTotalCount());
        assertNotNull(data.getTodayCount());
        assertNotNull(data.getWeekCount());
        assertNotNull(data.getMonthCount());
        
        // 验证数值不为负数
        assertTrue(data.getTotalCount() >= 0);
        assertTrue(data.getTodayCount() >= 0);
        assertTrue(data.getWeekCount() >= 0);
        assertTrue(data.getMonthCount() >= 0);
    }

    @Test
    public void testGetDailyStatistics() {
        List<Map<String, Object>> dailyStats = statisticService.getDailyStatistics(7);
        
        assertNotNull(dailyStats);
        assertTrue(dailyStats.size() >= 0);
        
        // 验证每个条目的结构
        for (Map<String, Object> stat : dailyStats) {
            assertNotNull(stat.get("date"));
            assertNotNull(stat.get("count"));
        }
    }

    @Test
    public void testGetWordTypeStatistics() {
        List<Map<String, Object>> wordTypeStats = statisticService.getWordTypeStatistics();
        
        assertNotNull(wordTypeStats);
        assertTrue(wordTypeStats.size() >= 0);
        
        // 验证每个条目的结构
        for (Map<String, Object> stat : wordTypeStats) {
            assertNotNull(stat.get("type"));
            assertNotNull(stat.get("count"));
        }
    }

    @Test
    public void testGetMemoryRate() {
        Double memoryRate = statisticService.getMemoryRate();
        
        assertNotNull(memoryRate);
        assertTrue(memoryRate >= 0.0);
        assertTrue(memoryRate <= 1.0); // 记忆率应该在0-1之间
    }

    @Test
    public void testGetUserStatistics() {
        Map<String, Object> userStats = statisticService.getUserStatistics(1L);
        
        assertNotNull(userStats);
        // 可以根据实际返回的字段进行断言
        // 例如：assertNotNull(userStats.get("totalWords"));
    }

    @Test
    public void testGetMonthlyStatistics() {
        List<Map<String, Object>> monthlyStats = statisticService.getMonthlyStatistics(12);
        
        assertNotNull(monthlyStats);
        assertTrue(monthlyStats.size() >= 0);
        assertTrue(monthlyStats.size() <= 12); // 最多12个月
        
        // 验证每个条目的结构
        for (Map<String, Object> stat : monthlyStats) {
            assertNotNull(stat.get("month"));
            assertNotNull(stat.get("count"));
        }
    }

    @Test
    public void testGetDifficultyStatistics() {
        Map<String, Object> difficultyStats = statisticService.getDifficultyStatistics();
        
        assertNotNull(difficultyStats);
        // 验证难度统计的结构
        // 例如：assertNotNull(difficultyStats.get("easy"));
        // assertNotNull(difficultyStats.get("medium"));
        // assertNotNull(difficultyStats.get("hard"));
    }

    @Test
    public void testStatisticDataConsistency() {
        StatisticData data = statisticService.getStatisticOverview();
        
        // 验证统计数据的逻辑一致性
        // 今日数量应该小于等于本周数量
        assertTrue(data.getTodayCount() <= data.getWeekCount());
        // 本周数量应该小于等于本月数量
        assertTrue(data.getWeekCount() <= data.getMonthCount());
        // 本月数量应该小于等于总数量
        assertTrue(data.getMonthCount() <= data.getTotalCount());
    }
}