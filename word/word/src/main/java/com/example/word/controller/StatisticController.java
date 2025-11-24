package com.example.word.controller;

import com.example.word.common.Result;
import com.example.word.common.StatisticData;
import com.example.word.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 * @author DaY1zz
 */
@RestController
@RequestMapping("/api/statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/overview")
    public Result<StatisticData> getStatisticOverview() {
        try {
            StatisticData data = statisticService.getStatisticOverview();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/daily/{days}")
    public Result<List<Map<String, Object>>> getDailyStatistics(@PathVariable Integer days) {
        try {
            List<Map<String, Object>> data = statisticService.getDailyStatistics(days);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取每日统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/word-type")
    public Result<List<Map<String, Object>>> getWordTypeStatistics() {
        try {
            List<Map<String, Object>> data = statisticService.getWordTypeStatistics();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取词汇类型统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/memory-rate")
    public Result<Double> getMemoryRate() {
        try {
            Double rate = statisticService.getMemoryRate();
            return Result.success(rate);
        } catch (Exception e) {
            return Result.error("获取记忆率失败: " + e.getMessage());
        }
    }
}