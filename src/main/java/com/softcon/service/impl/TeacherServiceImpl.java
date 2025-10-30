package com.softcon.service.impl;

import com.softcon.pojo.entity.Teacher;
import com.softcon.mapper.TeacherMapper;
import com.softcon.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Override
    public Teacher findByUsername(String username) {
        return teacherMapper.findByUsername(username);
    }
    
    @Override
    public Teacher login(String username, String password) {
        // 查询教师信息
        Teacher teacher = findByUsername(username);
        // 简单的密码验证（实际项目中应该使用加密）
        if (teacher != null && teacher.getPassword().equals(password)) {
            return teacher;
        }
        return null;
    }
}