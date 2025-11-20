package com.softcon.pojo.vo;

import com.softcon.entity.Question;
import com.softcon.entity.Submission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionVO {
    @Schema(description = "作业提交记录")
    private Submission submission;

    @Schema(description = "题目")
    private List<Question> questions;
}
