package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试卷实体类
 */
@Data
public class Paper {
    @Schema(description = "试卷ID")
    private Integer id;

    @Schema(description = "试卷名称")
    private String name;

    @Schema(description = "试卷描述")
    private String description;

    @Schema(description = "题目总数")
    private Integer totalQuestions;

    @Schema(description = "创建时间 yyyy-MM-dd")
    private String createdAt;
}