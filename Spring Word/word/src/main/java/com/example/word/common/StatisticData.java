package com.example.word.common;

import lombok.Data;
import java.util.List;

/**
 * 综合统计数据返回体
 * 封装了用户统计页面所需的各种数据
 */
@Data
public class StatisticData {
    /**
     * 总单词数
     * private: 私有成员变量
     */
    private Integer totalWords;

    /**
     * 已掌握的单词数
     * private: 私有成员变量
     */
    private Integer masteredWords;

    /**
     * 累计学习天数
     * private: 私有成员变量
     */
    private Integer learningDays;

    /**
     * 学习趋势数据 (用于折线图)
     * private: 私有成员变量
     */
    private List<LineData> studyTrend;

    /**
     * 单词掌握程度分布 (用于饼图)
     * private: 私有成员变量
     */
    private List<CircleData> wordDistribution;
}