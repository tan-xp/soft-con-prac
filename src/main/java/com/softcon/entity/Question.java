package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 题目实体类
 */
@Data
public class Question {
    @Schema(description = "题目ID")
    private Integer id;

    @Schema(description = "题目内容")
    private String question;

    @Schema(description = "正确答案")
    private Integer answer;

    @Schema(description = "运算符类型")
    private String operator;

    @Schema(description = "所属作业ID")
    private Integer assignmentId;
}