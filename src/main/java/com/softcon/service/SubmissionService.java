package com.softcon.service;

import com.softcon.entity.Submission;
import java.util.List;
import java.util.Map;

/**
 * 作业提交服务接口
 */
public interface SubmissionService {
    
    /**
     * 根据作业ID获取所有提交记录
     */
    List<Submission> getSubmissionsByAssignmentId(Integer assignmentId);
    
    /**
     * 根据学生ID获取所有提交记录
     */
    List<Submission> getSubmissionsByStudentId(Integer studentId);
    
    /**
     * 获取某个学生的作业提交记录
     */
    Submission getSubmissionByStudentAndAssignment(Integer studentId, Integer assignmentId);
    
    /**
     * 提交作业
     * @param studentId 学生ID
     * @param assignmentId 作业ID
     * @param answers 学生答案（JSON格式）
     * @param timeSpent 耗时（秒）
     * @return 包含提交结果的Map
     */
    Map<String, Object> submitAssignment(Integer studentId, Integer assignmentId, String answers, Integer timeSpent);
    
    /**
     * 更新提交记录
     */
    boolean updateSubmission(Submission submission);
    
    /**
     * 根据作业ID删除所有相关提交记录
     */
    boolean deleteSubmissionsByAssignmentId(Integer assignmentId);
}