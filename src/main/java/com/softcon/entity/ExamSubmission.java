package com.softcon.entity;

import lombok.Data;

/**
 * 考试提交记录实体类
 */
@Data
public class ExamSubmission {
    private Integer id;         // 提交记录ID
    private Integer examId;     // 考试ID
    private Integer studentId;  // 学生ID
    private String studentName; // 学生姓名
    private String startTime;   // 开始答题时间
    private String submitTime;  // 提交时间
    private Integer score;      // 得分
    private String status;      // 状态（未开始、进行中、已提交、已逾期）
    private String answers;     // 学生答案（JSON格式存储）
    private Integer usedTime;   // 用时（分钟）
}