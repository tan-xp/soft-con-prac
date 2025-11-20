package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作业提交记录实体类
 */
@Data
public class Submission {
    @Schema(description = "提交记录ID")
    private Integer id;

    @Schema(description = "作业ID")
    private Integer assignmentId;

    @Schema(description = "学生ID")
    private Integer studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "提交时间")
    private String submissionTime;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "状态（已提交/未提交）")
    private String status;

    @Schema(description = "学生答案（JSON格式存储）")
    private String answers;
}