package com.softcon.entity;

import lombok.Data;

@Data
public class ExamSubmission {
    private Integer id;
    private Integer examId;
    private Integer studentId;
    private String studentName;
    private String submissionTime;
    private Integer score;
    private String status; // pending/completed/overdue
    private String answers; // JSON
}