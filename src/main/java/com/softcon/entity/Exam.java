package com.softcon.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试实体类
 */
@Data
public class Exam {
    private Integer id;         // 考试ID
    private String title;       // 考试标题
    private String description; // 考试描述
    private String startTime;   // 开始时间
    private String endTime;     // 结束时间
    private Integer duration;   // 考试时长（分钟）
    private Integer totalScore; // 总分
    private String difficulty;  // 难度级别
    private String teacherName; // 教师姓名
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}