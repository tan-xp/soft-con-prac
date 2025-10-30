package com.softcon.service;

import com.softcon.entity.Assignment;

import java.util.List;

/**
 * 作业服务接口
 */
public interface AssignmentService {
    /**
     * 获取所有作业列表
     */
    List<Assignment> getAllAssignments();
    
    /**
     * 根据ID获取作业详情
     */
    Assignment getAssignmentById(Integer id);
    
    /**
     * 新增作业
     */
    boolean addAssignment(Assignment assignment);
    
    /**
     * 更新作业信息
     */
    boolean updateAssignment(Assignment assignment);
    
    /**
     * 删除作业
     */
    boolean deleteAssignment(Integer id);
}