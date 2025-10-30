package com.softcon.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * JSON工具类，用于在Thymeleaf中安全地将Java对象转换为JSON字符串
 */
@Component("jsonUtil")
public class JsonUtil {
    
    private final ObjectMapper objectMapper;
    
    public JsonUtil() {
        this.objectMapper = new ObjectMapper();
        // 配置ObjectMapper以处理常见的序列化问题
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 将对象转换为JSON字符串
     * @param obj 要转换的对象
     * @return JSON字符串，如果转换失败返回空字符串
     */
    public String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            // 记录异常但不抛出，确保页面不会崩溃
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * 安全地将对象转换为JSON字符串，处理null值
     * @param obj 要转换的对象
     * @return JSON字符串，如果对象为null或转换失败返回默认值
     */
    public String toJsonSafe(Object obj) {
        if (obj == null) {
            if (obj instanceof Collection<?> || obj instanceof Map<?, ?>) {
                return "[]";
            }
            return "{}";
        }
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            // 记录异常并返回安全的默认值
            e.printStackTrace();
            return obj instanceof Collection<?> || obj instanceof Map<?, ?> ? "[]" : "{}";
        }
    }
    
    /**
     * 将JSON字符串解析为对象
     * @param json JSON字符串
     * @param clazz 目标类
     * @param <T> 目标类型
     * @return 解析后的对象，如果解析失败返回null
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}