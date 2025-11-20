package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作业实体类
 */
@Data
public class Assignment {
    @Schema(description = "作业ID")
    private Integer id;
    
    @Schema(description = "作业标题")
    private String title;

    @Schema(description = "作业描述")
    private String description;

    @Schema(description = "题目总数")
    private Integer totalQuestions;

    @Schema(description = "截止日期")
    private String deadline;

    @Schema(description = "是否完成")
    private Boolean isCompleted;

    @Schema(description = "创建时间")
    private String createdAt;

    @Schema(description = "难度级别")
    private String difficulty;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "科目")
    private String subject;

    @Schema(description = "章节")
    private String chapter;
}