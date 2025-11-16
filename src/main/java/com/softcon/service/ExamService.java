package com.softcon.service;

import com.softcon.entity.Exam;

import java.util.List;

public interface ExamService {
    List<Exam> getAllExams();
    Exam getExamById(Integer id);
    boolean addExam(Exam exam);
    boolean updateExam(Exam exam);
    boolean deleteExam(Integer id);
}