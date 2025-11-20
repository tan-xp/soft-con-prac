package com.softcon.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitExamDTO {
    @Schema(description = "考试id")
    private Integer examId;

    @Schema(description = "学生id")
    private Integer studentId;

    @Schema(description = "作答答案")
    private Map<Integer,Integer> answers;
}
