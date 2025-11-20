package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考试实体类
 */
@Data
public class Exam {
    @Schema(description = "考试ID")
    private Integer id;

    @Schema(description = "考试标题")
    private String title;

    @Schema(description = "考试描述")
    private String description;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String startTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;

    @Schema(description = "状态：未开始/进行中/已结束")
    private String status;

    @Schema(description = "创建时间 yyyy-MM-dd")
    private String createdAt;

    @Schema(description = "关联试卷ID")
    private Integer paperId;
}