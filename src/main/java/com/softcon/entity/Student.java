package com.softcon.entity;

import lombok.Data;

import java.util.Date;

/**
 * 学生实体类
 */
@Data
public class Student {
    private Integer id;         // 学生ID
    private String username;    // 用户名
    private String password;    // 密码
    private String name;        // 学生姓名
    private String gender;      // 性别
    private String studentId;   // 学号
    private String phone;       // 联系电话
    private String email;       // 邮箱
    private String classInfo;   // 班级信息
    private Date createTime;    // 创建时间
    private Date updateTime;    // 更新时间
}