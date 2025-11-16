package com.softcon.entity;

import lombok.Data;

/**
 * 考试实体类
 */
@Data
public class Exam {
    private Integer id;           // 考试ID
    private String title;         // 考试标题
    private String description;   // 考试描述
    private String startTime;     // 开始时间 yyyy-MM-dd
    private String endTime;       // 结束时间 yyyy-MM-dd
    private String status;        // 状态：未开始/进行中/已结束
    private String createdAt;     // 创建时间 yyyy-MM-dd
    private Integer paperId;      // 关联试卷ID
}