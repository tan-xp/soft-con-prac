package com.softcon.service.impl;

import com.softcon.entity.Assignment;
import com.softcon.mapper.AssignmentMapper;
import com.softcon.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 作业服务实现类
 */
@Service
public class AssignmentServiceImpl implements AssignmentService {
    
    @Autowired
    private AssignmentMapper assignmentMapper;
    
    @Override
    public List<Assignment> getAllAssignments() {
        return assignmentMapper.getAllAssignments();
    }
    
    @Override
    public Assignment getAssignmentById(Integer id) {
        return assignmentMapper.getAssignmentById(id);
    }
    
    @Override
    public boolean addAssignment(Assignment assignment) {
        // 设置创建时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assignment.setCreatedAt(dateFormat.format(new Date()));
        assignment.setIsCompleted(false);
        return assignmentMapper.insert(assignment) > 0;
    }
    
    @Override
    public boolean updateAssignment(Assignment assignment) {
        return assignmentMapper.update(assignment) > 0;
    }
    
    @Override
    public boolean deleteAssignment(Integer id) {
        return assignmentMapper.delete(id) > 0;
    }
}