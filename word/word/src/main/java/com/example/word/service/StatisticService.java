package com.example.word.service;

import com.example.word.common.StatisticData;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 * @author DaY1zz
 */
public interface StatisticService {

    StatisticData getStatisticOverview();

    List<Map<String, Object>> getDailyStatistics(Integer days);

    List<Map<String, Object>> getWordTypeStatistics();

    Double getMemoryRate();

    Map<String, Object> getUserStatistics(Long userId);

    List<Map<String, Object>> getMonthlyStatistics(Integer months);

    Map<String, Object> getDifficultyStatistics();
}