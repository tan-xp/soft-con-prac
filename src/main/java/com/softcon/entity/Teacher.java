package com.softcon.entity;

import lombok.Data;

/**
 * 教师实体类
 */
@Data
public class Teacher {
    private Integer id;         // 教师ID
    private String username;    // 用户名
    private String password;    // 密码
    private String name;        // 教师姓名
    private String phone;       // 联系电话
    private String email;       // 邮箱
}