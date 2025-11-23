package com.example.word.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计图表数据模型 - 用于折线图
 * 例如：每日学习数量趋势
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineData {
    /**
     * 日期或时间点
     * private: 私有成员变量
     */
    private String date;

    /**
     * 对应的数量
     * private: 私有成员变量
     */
    private Integer count;
}