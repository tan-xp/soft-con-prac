package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 学生实体类
 */
@Data
public class Student {
    @Schema(description = "学生ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "学生姓名")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "班级信息")
    private String classInfo;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}