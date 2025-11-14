package com.softcon.entity;

import lombok.Data;

/**
 * 试卷题目关系实体类
 * 用于表示试卷和题目的多对多关系，并记录题目在试卷中的分值等信息
 */
@Data
public class PaperQuestion {
    private Integer id;         // 主键ID
    private Integer paperId;    // 试卷ID
    private Integer questionId; // 题目ID
    private Integer score;      // 本题分值
    private Integer sortOrder;  // 题目排序
    private String question;    // 题目内容
    private Integer answer;     // 正确答案
}