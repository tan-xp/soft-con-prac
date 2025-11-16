package com.softcon.mapper;

import com.softcon.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    List<Exam> getAllExams();
    Exam getExamById(@Param("id") Integer id);
    int insert(Exam exam);
    int update(Exam exam);
    int delete(@Param("id") Integer id);
}