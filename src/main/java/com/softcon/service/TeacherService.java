package com.softcon.service;

import com.softcon.entity.Teacher;

/**
 * 教师服务接口
 */
public interface TeacherService {
    /**
     * 根据用户名查询教师信息
     * @param username 用户名
     * @return 教师对象
     */
    Teacher findByUsername(String username);
    
    /**
     * 教师登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回教师对象，失败返回null
     */
    Teacher login(String username, String password);
}