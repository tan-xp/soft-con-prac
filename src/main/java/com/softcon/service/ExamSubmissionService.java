package com.softcon.service;

import com.softcon.entity.ExamSubmission;

import java.util.List;

public interface ExamSubmissionService {
    ExamSubmission getByExamAndStudent(Integer examId, Integer studentId);
    List<ExamSubmission> getByExamId(Integer examId);
    List<ExamSubmission> getByStudentId(Integer studentId);
    boolean insert(ExamSubmission submission);
    boolean update(ExamSubmission submission);
}