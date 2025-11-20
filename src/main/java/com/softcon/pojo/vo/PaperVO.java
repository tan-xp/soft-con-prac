package com.softcon.pojo.vo;

import com.softcon.entity.Exam;
import com.softcon.entity.Paper;
import com.softcon.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaperVO {
    @Schema(description = "考试信息")
    private Exam exam;

    @Schema(description = "试卷信息")
    private Paper paper;

    @Schema(description = "试题信息")
    private List<Question> questions;

    @Schema(description = "试题分值信息")
    private Map<Integer, Integer> scores;

    @Schema(description = "访问控制")
    private Boolean accessible;
}
