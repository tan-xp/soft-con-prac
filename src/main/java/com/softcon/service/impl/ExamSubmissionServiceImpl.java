package com.softcon.service.impl;

import com.softcon.entity.ExamSubmission;
import com.softcon.mapper.ExamSubmissionMapper;
import com.softcon.service.ExamSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamSubmissionServiceImpl implements ExamSubmissionService {

    @Autowired
    private ExamSubmissionMapper examSubmissionMapper;

    @Override
    public ExamSubmission getByExamAndStudent(Integer examId, Integer studentId) {
        return examSubmissionMapper.getByExamAndStudent(examId, studentId);
    }

    @Override
    public List<ExamSubmission> getByExamId(Integer examId) {
        return examSubmissionMapper.getByExamId(examId);
    }

    @Override
    public List<ExamSubmission> getByStudentId(Integer studentId) {
        return examSubmissionMapper.getByStudentId(studentId);
    }

    @Override
    public boolean insert(ExamSubmission submission) {
        return examSubmissionMapper.insert(submission) > 0;
    }

    @Override
    public boolean update(ExamSubmission submission) {
        return examSubmissionMapper.update(submission) > 0;
    }
}