package com.softcon.entity;

import lombok.Data;

/**
 * 作业提交记录实体类
 */
@Data
public class Submission {
    private Integer id;         // 提交记录ID
    private Integer assignmentId; // 作业ID
    private Integer studentId;  // 学生ID
    private String studentName; // 学生姓名
    private String submissionTime; // 提交时间
    private Integer score;      // 得分
    private String status;      // 状态（已提交/未提交）
    private String answers;     // 学生答案（JSON格式存储）
}