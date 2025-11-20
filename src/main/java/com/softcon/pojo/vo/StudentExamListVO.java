package com.softcon.pojo.vo;

import com.softcon.entity.Exam;
import com.softcon.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentExamListVO {
    @Schema(description = "考试ID")
    private Integer examId;

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

    @Schema(description = "访问控制")
    private boolean accessible;

    @Schema(description = "是否提交")
    private boolean submitted;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "提交时间")
    private String submissionTime;
}
