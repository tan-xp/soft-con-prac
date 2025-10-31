package com.softcon.mapper;

import com.softcon.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交记录Mapper接口
 */
@Mapper
public interface SubmissionMapper {
    /**
     * 根据作业ID获取所有提交记录
     */
    List<Submission> getSubmissionsByAssignmentId(@Param("assignmentId") Integer assignmentId);
    
    /**
     * 获取某个学生的作业提交记录
     */
    Submission getSubmissionByStudentAndAssignment(@Param("studentId") Integer studentId, 
                                                 @Param("assignmentId") Integer assignmentId);
    
    /**
     * 插入提交记录
     */
    int insert(Submission submission);
    
    /**
     * 更新提交记录
     */
    int update(Submission submission);
    
    /**
     * 根据作业ID删除所有相关提交记录
     */
    int deleteByAssignmentId(@Param("assignmentId") Integer assignmentId);
    
    /**
     * 根据学生ID获取所有提交记录
     */
    List<Submission> getSubmissionsByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * 根据ID获取提交记录
     */
    Submission getSubmissionById(@Param("id") Integer id);
}