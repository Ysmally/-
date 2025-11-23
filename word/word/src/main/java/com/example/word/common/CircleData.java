package com.example.word.common;

import lombok.Data;

/**
 * 圆形数据模型
 * @author DaY1zz
 */
@Data
public class CircleData {
    private String name;
    private Double value;
    
    public CircleData() {}
    
    public CircleData(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}