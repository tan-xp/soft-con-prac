package com.softcon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcon.entity.*;
import com.softcon.mapper.PaperQuestionMapper;
import com.softcon.mapper.QuestionMapper;
import com.softcon.mapper.StudentMapper;
import com.softcon.pojo.Result;
import com.softcon.pojo.dto.SubmitExamDTO;
import com.softcon.pojo.vo.PaperVO;
import com.softcon.pojo.vo.StudentExamListVO;
import com.softcon.pojo.vo.StudentExamScoreVO;
import com.softcon.service.ExamService;
import com.softcon.service.ExamSubmissionService;
import com.softcon.service.PaperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/exam/student")
@Tag(name = "学生考试相关接口")
public class StudentExamController {

    @Autowired
    private ExamService examService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private ExamSubmissionService examSubmissionService;
    @Autowired
    private StudentMapper studentMapper;

    @RequestMapping(value = "/paper/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary ="获取考试试卷及试题")
    public Result<PaperVO> getExamPaper(@PathVariable("id") Integer examId) {
        try {
            Exam exam = examService.getExamById(examId);
            if (exam == null) throw new RuntimeException("考试不存在");

            Paper paper = paperService.getPaperById(exam.getPaperId());
            List<Question> questions = questionMapper.getQuestionsByPaperId(paper.getId());
            List<PaperQuestion> pqs = paperQuestionMapper.getByPaperId(paper.getId());
            Map<Integer, Integer> scores = new HashMap<>();
            for (PaperQuestion pq : pqs) {
                scores.put(pq.getQuestionId(), pq.getScore() == null ? 0 : pq.getScore());
            }

            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date s; Date e;
            try { s = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { s = fmtDb.parse(exam.getStartTime()); }
            try { e = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { e = fmtDb.parse(exam.getEndTime()); }

            return Result.success(PaperVO.builder()
                            .exam(exam)
                            .paper(paper)
                            .questions(questions)
                            .scores(scores)
                            .accessible(!now.before(s) && !now.after(e))
                    .build());
        } catch (Exception e) {
            return Result.error("获取考试试卷失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary ="提交考试答卷")
    public Map<String, Object> submitExam(@RequestBody SubmitExamDTO submitExamDTO) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer examId = submitExamDTO.getExamId();
            Integer studentId = submitExamDTO.getStudentId();
            String answersJson = new ObjectMapper().writeValueAsString(submitExamDTO.getAnswers());

            Exam exam = examService.getExamById(examId);
            if (exam == null) throw new RuntimeException("考试不存在");

            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date s; Date e;
            try { s = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { s = fmtDb.parse(exam.getStartTime()); }
            try { e = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { e = fmtDb.parse(exam.getEndTime()); }
            if (now.before(s)) throw new RuntimeException("考试未开始");
            if (now.after(e)) throw new RuntimeException("考试已结束，无法提交");

            Paper paper = paperService.getPaperById(exam.getPaperId());
            List<Question> questions = questionMapper.getQuestionsByPaperId(paper.getId());
            List<PaperQuestion> pqs = paperQuestionMapper.getByPaperId(paper.getId());
            Map<Integer, Integer> scoreMap = new HashMap<>();
            for (PaperQuestion pq : pqs) {
                scoreMap.put(pq.getQuestionId(), pq.getScore() == null ? 0 : pq.getScore());
            }

            ObjectMapper om = new ObjectMapper();
            JsonNode answersNode = om.readTree(answersJson);
            int total = 0;
            for (Question q : questions) {
                JsonNode ans = answersNode.get(String.valueOf(q.getId()));
                if (ans != null && ans.isInt() && ans.asInt() == q.getAnswer()) {
                    total += scoreMap.getOrDefault(q.getId(), 0);
                }
            }

            ExamSubmission existing = examSubmissionService.getByExamAndStudent(examId, studentId);
            Student student = studentMapper.findById(studentId);
            String nowStr = fmtDb.format(now);
            if (existing == null) {
                ExamSubmission submission = new ExamSubmission();
                submission.setExamId(examId);
                submission.setStudentId(studentId);
                submission.setStudentName(student != null ? student.getName() : "");
                submission.setSubmissionTime(nowStr);
                submission.setScore(total);
                submission.setStatus("completed");
                submission.setAnswers(answersJson);
                examSubmissionService.insert(submission);
            } else {
                existing.setSubmissionTime(nowStr);
                existing.setScore(total);
                existing.setStatus("completed");
                existing.setAnswers(answersJson);
                examSubmissionService.update(existing);
            }

            result.put("success", true);
            result.put("message", "提交成功");
            result.put("score", total);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/submission/{examId}/{studentId}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary ="获取考试提交记录")
    public Result<ExamSubmission> getSubmission(@PathVariable("examId") Integer examId, @PathVariable("studentId") Integer studentId) {
        try {
            ExamSubmission sub = examSubmissionService.getByExamAndStudent(examId, studentId);

            return Result.success(sub);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary ="获取学生考试列表")
    public Result<List<StudentExamListVO>> listStudentExams(@RequestParam("studentId") Integer studentId,
                                                      @RequestParam(value = "status", required = false) String status) {
        try {
            List<Exam> exams = examService.getAllExams();
            if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
                exams = exams.stream().filter(e -> status.equals(e.getStatus())).collect(java.util.stream.Collectors.toList());
            }
            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            List<StudentExamListVO> data = new ArrayList<>();
            for (Exam exam : exams) {
                Date s; Date e;
                try { s = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { s = fmtDb.parse(exam.getStartTime()); }
                try { e = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { e = fmtDb.parse(exam.getEndTime()); }
                boolean accessible = !now.before(s) && !now.after(e);
                ExamSubmission sub = examSubmissionService.getByExamAndStudent(exam.getId(), studentId);

                StudentExamListVO studentExamListVO=StudentExamListVO.builder()
                        .examId(exam.getId())
                        .title(exam.getTitle())
                        .description(exam.getDescription())
                        .startTime(exam.getStartTime())
                        .endTime(exam.getEndTime())
                        .status(exam.getStatus())
                        .accessible(accessible)
                        .build();

                if (sub != null) {
                    studentExamListVO.setSubmitted("completed".equalsIgnoreCase(sub.getStatus()));
                    studentExamListVO.setScore(sub.getScore());
                    studentExamListVO.setSubmissionTime(sub.getSubmissionTime());
                } else {
                    studentExamListVO.setSubmitted(false);
                    studentExamListVO.setScore(0);
                    studentExamListVO.setSubmissionTime(null);
                }
                data.add(studentExamListVO);
            }

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取考试列表失败：" + e.getMessage());
        }
    }

    @RequestMapping(value = "/scores/{studentId}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary ="获取学生考试成绩")
    public Result<List<StudentExamScoreVO>> listStudentExamScores(@PathVariable("studentId") Integer studentId) {
        try {
            List<ExamSubmission> submissions = examSubmissionService.getByStudentId(studentId);
            List<StudentExamScoreVO> data = new ArrayList<>();
            for (ExamSubmission sub : submissions) {
                Exam exam = examService.getExamById(sub.getExamId());

                StudentExamScoreVO studentExamScoreVO=StudentExamScoreVO.builder()
                        .examId(sub.getExamId())
                        .title(exam != null ? exam.getTitle() : "")
                        .startTime(exam != null ? exam.getStartTime() : "")
                        .endTime(exam != null ? exam.getEndTime() : "")
                        .submitted(sub.getStatus() != null && sub.getStatus().equalsIgnoreCase("completed"))
                        .score(sub.getScore() != null ? sub.getScore() : 0)
                        .submissionTime(sub.getSubmissionTime())
                        .build();

                int totalCount = 0;
                int correctCount = 0;
                if (exam != null) {
                    List<Question> qs = questionMapper.getQuestionsByPaperId(exam.getPaperId());
                    totalCount = qs != null ? qs.size() : 0;
                    if (sub.getAnswers() != null && totalCount > 0) {
                        try {
                            ObjectMapper om = new ObjectMapper();
                            JsonNode node = om.readTree(sub.getAnswers());
                            for (Question q : qs) {
                                JsonNode ans = node.get(String.valueOf(q.getId()));
                                if (ans != null && ans.isInt() && ans.asInt() == q.getAnswer()) {
                                    correctCount++;
                                }
                            }
                        } catch (Exception ignore) {}
                    }
                }
                studentExamScoreVO.setCorrectCount(correctCount);
                studentExamScoreVO.setTotalCount(totalCount);

                data.add(studentExamScoreVO);
            }
            return Result.success(data);
        } catch (Exception e) {

            return Result.error("获取考试成绩失败：" + e.getMessage());
        }
    }
}