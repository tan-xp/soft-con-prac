package com.softcon.mapper;

import com.softcon.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper {
    /**
     * 查询所有学生信息
     * @return 学生列表
     */
    List<Student> findAll();
    
    /**
     * 根据ID查询学生信息
     * @param id 学生ID
     * @return 学生对象
     */
    Student findById(@Param("id") Integer id);
    
    /**
     * 根据用户名查询学生信息
     * @param username 用户名
     * @return 学生对象
     */
    Student findByUsername(@Param("username") String username);
    
    /**
     * 新增学生信息
     * @param student 学生对象
     * @return 影响行数
     */
    int insert(Student student);
    
    /**
     * 更新学生信息
     * @param student 学生对象
     * @return 影响行数
     */
    int update(Student student);
    
    /**
     * 删除学生信息
     * @param id 学生ID
     * @return 影响行数
     */
    int delete(@Param("id") Integer id);
    
    /**
     * 根据条件查询学生信息
     * @param keyword 搜索关键字
     * @return 学生列表
     */
    List<Student> search(@Param("keyword") String keyword);
    
    /**
     * 根据学号和密码查询学生
     * @param studentId 学号
     * @param password 密码
     * @return 学生对象，不存在返回null
     */
    Student findByStudentIdAndPassword(@Param("studentId") String studentId, @Param("password") String password);
    
    /**
     * 根据学号查询学生
     * @param studentId 学号
     * @return 学生对象，不存在返回null
     */
    Student findByStudentId(@Param("studentId") String studentId);
}