package com.example.word.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计图表数据模型 - 用于柱状图
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnData {
    /**
     * X轴类目数据
     * private: 私有成员变量
     */
    private String x;

    /**
     * Y轴数值
     * private: 私有成员变量
     */
    private Integer y;
}