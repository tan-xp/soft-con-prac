package com.softcon.mapper;

import com.softcon.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper {
    /**
     * 根据用户名查询教师信息
     * @param username 用户名
     * @return 教师对象
     */
    Teacher findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询教师信息
     * @param id 教师ID
     * @return 教师对象
     */
    Teacher findById(@Param("id") Integer id);
}