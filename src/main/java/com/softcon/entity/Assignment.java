package com.softcon.entity;

import lombok.Data;

import java.util.Date;

/**
 * 作业实体类
 */
@Data
public class Assignment {
    private Integer id;         // 作业ID
    private String title;       // 作业标题
    private String description; // 作业描述
    private Integer totalQuestions; // 题目总数
    private String deadline;    // 截止日期
    private Boolean isCompleted; // 是否完成
    private String createdAt;   // 创建时间
    private String difficulty;  // 难度级别
    private String teacherName; // 教师姓名
    private String subject;     // 科目
    private String chapter;     // 章节
}