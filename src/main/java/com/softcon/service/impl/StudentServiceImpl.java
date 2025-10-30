package com.softcon.service.impl;

import com.softcon.entity.Student;
import com.softcon.mapper.StudentMapper;
import com.softcon.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Override
    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }
    
    @Override
    public Student getStudentById(Integer id) {
        return studentMapper.findById(id);
    }
    
    @Override
    public boolean addStudent(Student student) {
        // 检查用户名是否已存在
        if (isUsernameExist(student.getUsername(), null)) {
            return false;
        }
        // 设置默认密码（如果未设置）
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            // 默认密码可以设置为学号后6位或其他规则
            student.setPassword("123456"); // 简单默认密码，实际项目中应考虑更安全的策略
        }
        return studentMapper.insert(student) > 0;
    }
    
    @Override
    public boolean updateStudent(Student student) {
        // 检查用户名是否已被其他学生使用
        if (isUsernameExist(student.getUsername(), student.getId())) {
            return false;
        }
        return studentMapper.update(student) > 0;
    }
    
    @Override
    public boolean deleteStudent(Integer id) {
        return studentMapper.delete(id) > 0;
    }
    
    @Override
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentMapper.search(keyword.trim());
    }
    
    @Override
    public boolean isUsernameExist(String username, Integer excludeId) {
        Student student = studentMapper.findByUsername(username);
        if (student == null) {
            return false;
        }
        // 如果排除了当前学生ID，则检查是否为同一个学生
        if (excludeId != null && student.getId().equals(excludeId)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean resetPassword(Integer id, String newPassword) {
        Student student = getStudentById(id);
        if (student == null) {
            return false;
        }
        student.setPassword(newPassword);
        return updateStudent(student);
    }
    
    @Override
    public Student login(String studentId, String password) {
        // 调用Mapper根据学号和密码查询学生
        return studentMapper.findByStudentIdAndPassword(studentId, password);
    }
    
    @Override
    public Student getStudentByStudentId(String studentId) {
        // 调用Mapper根据学号查询学生
        return studentMapper.findByStudentId(studentId);
    }
}