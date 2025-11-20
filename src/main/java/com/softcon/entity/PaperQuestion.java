package com.softcon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 试卷-题目关联实体
 */
@Data
public class PaperQuestion {
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "试卷ID")
    private Integer paperId;

    @Schema(description = "题目ID")
    private Integer questionId;

    @Schema(description = "该题分值（试卷内）")
    private Integer score;
}