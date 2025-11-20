package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 教师实体类
 */
@Data
public class Teacher {
    @Schema(description = "教师ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "教师姓名")
    private String name;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;
}