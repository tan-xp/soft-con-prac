package com.softcon.entity;

import lombok.Data;

/**
 * 试卷-题目关联实体
 */
@Data
public class PaperQuestion {
    private Integer id;        // 主键ID
    private Integer paperId;   // 试卷ID
    private Integer questionId;// 题目ID
    private Integer score;     // 该题分值（试卷内）
}