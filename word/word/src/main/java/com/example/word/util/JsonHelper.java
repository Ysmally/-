package com.example.word.util;

import com.alibaba.fastjson.JSON;

/**
 * JSON工具类
 * 基于FastJSON的简单封装
 */
public class JsonHelper {
    /**
     * 对象转JSON字符串
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * JSON字符串转对象
     * @param json JSON字符串
     * @param clazz 目标类
     * @return 转换后的对象
     */
    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}