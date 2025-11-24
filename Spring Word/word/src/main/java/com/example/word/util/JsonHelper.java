package com.example.word.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 处理工具类，基于 Jackson 库
 * @Slf4j 注解为类自动创建一个名为 log 的 SLF4J logger 实例
 */
@Slf4j
public class JsonHelper {
    /**
     * 创建一个静态、线程安全的ObjectMapper实例
     * private static final: 私有、静态、不可变的成员变量
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将Java对象转换为JSON字符串
     * @param object 要转换的对象
     * @return JSON格式的字符串，如果转换失败则返回null
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error", e); // 记录序列化错误日志
            return null;
        }
    }

    /**
     * 将JSON字符串解析为Java对象
     * @param json 要解析的JSON字符串
     * @param clazz 目标对象的Class类型
     * @return 解析后的Java对象，如果解析失败则返回null
     */
    public static <T> T parse(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON deserialization error", e); // 记录反序列化错误日志
            return null;
        }
    }
}