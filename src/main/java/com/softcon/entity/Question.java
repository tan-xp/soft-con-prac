package com.softcon.entity;

import lombok.Data;

/**
 * 题目实体类
 */
@Data
public class Question {
    private Integer id;         // 题目ID
    private String question;    // 题目内容
    private Integer answer;     // 正确答案
    private String operator;    // 运算符类型
    private Integer assignmentId; // 所属作业ID
}