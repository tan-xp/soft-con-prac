package com.softcon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcon.entity.*;
import com.softcon.mapper.PaperQuestionMapper;
import com.softcon.mapper.QuestionMapper;
import com.softcon.mapper.StudentMapper;
import com.softcon.service.ExamService;
import com.softcon.service.ExamSubmissionService;
import com.softcon.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/exam/student")
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
    public Map<String, Object> getExamPaper(@PathVariable("id") Integer examId) {
        Map<String, Object> result = new HashMap<>();
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

            Map<String, Object> data = new HashMap<>();
            data.put("exam", exam);
            data.put("paper", paper);
            data.put("questions", questions);
            data.put("scores", scores);

            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date s; Date e;
            try { s = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { s = fmtDb.parse(exam.getStartTime()); }
            try { e = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { e = fmtDb.parse(exam.getEndTime()); }
            data.put("accessible", !now.before(s) && !now.after(e));

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取考试试卷失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitExam(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer examId = Integer.parseInt(body.get("examId").toString());
            Integer studentId = Integer.parseInt(body.get("studentId").toString());
            String answersJson = new ObjectMapper().writeValueAsString(body.get("answers"));

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
    public Map<String, Object> getSubmission(@PathVariable("examId") Integer examId, @PathVariable("studentId") Integer studentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            ExamSubmission sub = examSubmissionService.getByExamAndStudent(examId, studentId);
            result.put("success", true);
            result.put("data", sub);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listStudentExams(@RequestParam("studentId") Integer studentId,
                                                @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Exam> exams = examService.getAllExams();
            if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
                exams = exams.stream().filter(e -> status.equals(e.getStatus())).collect(java.util.stream.Collectors.toList());
            }
            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            List<Map<String, Object>> data = new ArrayList<>();
            for (Exam exam : exams) {
                Date s; Date e;
                try { s = fmtIso.parse(exam.getStartTime()); } catch (Exception pe) { s = fmtDb.parse(exam.getStartTime()); }
                try { e = fmtIso.parse(exam.getEndTime()); } catch (Exception pe) { e = fmtDb.parse(exam.getEndTime()); }
                boolean accessible = !now.before(s) && !now.after(e);
                ExamSubmission sub = examSubmissionService.getByExamAndStudent(exam.getId(), studentId);
                Map<String, Object> item = new HashMap<>();
                item.put("examId", exam.getId());
                item.put("title", exam.getTitle());
                item.put("description", exam.getDescription());
                item.put("startTime", exam.getStartTime());
                item.put("endTime", exam.getEndTime());
                item.put("status", exam.getStatus());
                item.put("accessible", accessible);
                if (sub != null) {
                    item.put("submitted", "completed".equalsIgnoreCase(sub.getStatus()));
                    item.put("score", sub.getScore());
                    item.put("submissionTime", sub.getSubmissionTime());
                } else {
                    item.put("submitted", false);
                    item.put("score", 0);
                    item.put("submissionTime", null);
                }
                data.add(item);
            }
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取考试列表失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/scores/{studentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listStudentExamScores(@PathVariable("studentId") Integer studentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ExamSubmission> submissions = examSubmissionService.getByStudentId(studentId);
            List<Map<String, Object>> data = new ArrayList<>();
            for (ExamSubmission sub : submissions) {
                Exam exam = examService.getExamById(sub.getExamId());
                Map<String, Object> item = new HashMap<>();
                item.put("examId", sub.getExamId());
                item.put("title", exam != null ? exam.getTitle() : "");
                item.put("startTime", exam != null ? exam.getStartTime() : "");
                item.put("endTime", exam != null ? exam.getEndTime() : "");
                item.put("submitted", sub.getStatus() != null && sub.getStatus().equalsIgnoreCase("completed"));
                item.put("score", sub.getScore() != null ? sub.getScore() : 0);
                item.put("submissionTime", sub.getSubmissionTime());
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
                item.put("correctCount", correctCount);
                item.put("totalCount", totalCount);
                data.add(item);
            }
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取考试成绩失败：" + e.getMessage());
        }
        return result;
    }
}