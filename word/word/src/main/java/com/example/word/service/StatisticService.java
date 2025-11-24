// src/main/java/com/example/word/service/StatisticService.java
package com.example.word.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.word.common.CircleData;
import com.example.word.common.LineData;
import com.example.word.common.StatisticData;
import com.example.word.entity.DateRecord;
import com.example.word.entity.MemoryRecord;
import com.example.word.mapper.DateRecordMapper;
import com.example.word.mapper.MemoryRecordMapper;
import com.example.word.mapper.WordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    @Autowired
    private DateRecordMapper dateRecordMapper;

    @Autowired
    private MemoryRecordMapper memoryRecordMapper;

    @Autowired
    private WordMapper wordMapper;

    public StatisticData getUserStatistic(String userID) {
        StatisticData data = new StatisticData();

        // 1. 总单词量（可后续按用户选择的词书过滤）
        data.setTotalWords(Math.toIntExact(wordMapper.selectCount(null)));

        // 2. 已掌握单词数（memory_level > 4 算掌握）
        QueryWrapper<MemoryRecord> masteredQw = new QueryWrapper<>();
        masteredQw.eq("user_id", userID).gt("memory_level", 4);
        data.setMasteredWords(Math.toIntExact(memoryRecordMapper.selectCount(masteredQw)));

        // 3. 累计学习天数
        QueryWrapper<DateRecord> dayQw = new QueryWrapper<>();
        dayQw.eq("user_id", userID);
        data.setLearningDays(Math.toIntExact(dateRecordMapper.selectCount(dayQw)));

        // 4. 最近7天学习趋势（折线图）
        QueryWrapper<DateRecord> trendQw = new QueryWrapper<>();
        trendQw.eq("user_id", userID)
                .orderByDesc("date")
                .last("LIMIT 7");
        List<DateRecord> recent = dateRecordMapper.selectList(trendQw);

        List<LineData> trend = recent.stream()
                .map(r -> new LineData(
                        r.getDate().toString(),
                        r.getLearnCount() + r.getReviewCount()
                ))
                .collect(Collectors.toList());
        data.setStudyTrend(trend);

        // 5. 掌握分布（饼图）
        List<CircleData> distribution = new ArrayList<>();

        QueryWrapper<MemoryRecord> reviewingQw = new QueryWrapper<>();
        reviewingQw.eq("user_id", userID).le("memory_level", 4);
        Long reviewingCount = memoryRecordMapper.selectCount(reviewingQw);

        distribution.add(new CircleData("学习中", reviewingCount.doubleValue()));
        distribution.add(new CircleData("已掌握", (double) data.getMasteredWords()));
        data.setWordDistribution(distribution);

        return data;
    }
}