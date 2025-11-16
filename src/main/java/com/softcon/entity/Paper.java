package com.softcon.entity;

import lombok.Data;

/**
 * 试卷实体类
 */
@Data
public class Paper {
    private Integer id;            // 试卷ID
    private String name;           // 试卷名称
    private String description;    // 试卷描述
    private Integer totalQuestions;// 题目总数
    private String createdAt;      // 创建时间 yyyy-MM-dd
}