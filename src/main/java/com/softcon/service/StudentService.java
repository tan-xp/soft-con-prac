package com.softcon.service;

import com.softcon.entity.Student;

import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService {
    /**
     * 获取所有学生信息
     * @return 学生列表
     */
    List<Student> getAllStudents();
    
    /**
     * 根据ID获取学生信息
     * @param id 学生ID
     * @return 学生对象
     */
    Student getStudentById(Integer id);
    
    /**
     * 添加学生信息
     * @param student 学生对象
     * @return 是否添加成功
     */
    boolean addStudent(Student student);
    
    /**
     * 更新学生信息
     * @param student 学生对象
     * @return 是否更新成功
     */
    boolean updateStudent(Student student);
    
    /**
     * 删除学生信息
     * @param id 学生ID
     * @return 是否删除成功
     */
    boolean deleteStudent(Integer id);
    
    /**
     * 搜索学生信息
     * @param keyword 搜索关键字
     * @return 学生列表
     */
    List<Student> searchStudents(String keyword);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @param excludeId 排除的学生ID（用于更新时）
     * @return 是否存在
     */
    boolean isUsernameExist(String username, Integer excludeId);
    
    /**
     * 重置学生密码
     * @param id 学生ID
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(Integer id, String newPassword);
    
    /**
     * 学生登录验证
     * @param studentId 学号
     * @param password 密码
     * @return 登录成功返回学生对象，失败返回null
     */
    Student login(String studentId, String password);
    
    /**
     * 根据学号获取学生信息
     * @param studentId 学号
     * @return 学生对象，不存在返回null
     */
    Student getStudentByStudentId(String studentId);
}