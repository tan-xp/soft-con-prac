package com.softcon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷库实体类
 * 用于存储预定义的试卷模板
 */
@Data
public class Paper {
    private Integer id;         // 试卷ID
    private String title;       // 试卷标题
    private String description; // 试卷描述
    private Integer totalQuestions; // 题目总数
    private Integer totalScore; // 总分
    private String difficulty;  // 难度级别
    private String teacherName; // 创建者姓名
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}