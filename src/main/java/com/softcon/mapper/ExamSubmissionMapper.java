package com.softcon.mapper;

import com.softcon.entity.ExamSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamSubmissionMapper {
    ExamSubmission getByExamAndStudent(@Param("examId") Integer examId, @Param("studentId") Integer studentId);
    List<ExamSubmission> getByExamId(@Param("examId") Integer examId);
    List<ExamSubmission> getByStudentId(@Param("studentId") Integer studentId);
    int insert(ExamSubmission submission);
    int update(ExamSubmission submission);
}