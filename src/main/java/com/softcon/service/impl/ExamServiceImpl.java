package com.softcon.service.impl;

import com.softcon.entity.Exam;
import com.softcon.mapper.ExamMapper;
import com.softcon.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<Exam> getAllExams() {
        return examMapper.getAllExams();
    }

    @Override
    public Exam getExamById(Integer id) {
        return examMapper.getExamById(id);
    }

    @Override
    public boolean addExam(Exam exam) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        exam.setCreatedAt(df.format(new Date()));
        return examMapper.insert(exam) > 0;
    }

    @Override
    public boolean updateExam(Exam exam) {
        return examMapper.update(exam) > 0;
    }

    @Override
    public boolean deleteExam(Integer id) {
        return examMapper.delete(id) > 0;
    }
}