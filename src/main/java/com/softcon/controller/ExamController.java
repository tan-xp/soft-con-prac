package com.softcon.controller;

import com.softcon.entity.Exam;
import com.softcon.entity.Paper;
import com.softcon.entity.PaperQuestion;
import com.softcon.entity.Question;
import com.softcon.entity.ExamSubmission;
import com.softcon.entity.Student;
import com.softcon.mapper.PaperQuestionMapper;
import com.softcon.mapper.QuestionMapper;
import com.softcon.service.ExamService;
import com.softcon.service.PaperService;
import com.softcon.service.ExamSubmissionService;
import com.softcon.mapper.StudentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/exam")
public class ExamController {

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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toExamList(Model model, HttpSession session, @RequestParam(value = "keyword", required = false) String keyword) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);

        List<Exam> exams = examService.getAllExams();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.toLowerCase();
            exams = exams.stream().filter(e ->
                    (e.getTitle() != null && e.getTitle().toLowerCase().contains(k)) ||
                    (e.getDescription() != null && e.getDescription().toLowerCase().contains(k))
            ).collect(java.util.stream.Collectors.toList());
        }
        model.addAttribute("exams", exams);
        return "exam/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toAddExam(Model model, HttpSession session, @RequestParam(value = "id", required = false) Integer id) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        List<Paper> papers = paperService.getAllPapers();
        model.addAttribute("papers", papers);
        if (id != null) {
            Exam exam = examService.getExamById(id);
            model.addAttribute("exam", exam);
        }
        return "exam/add";
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllExams() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", examService.getAllExams());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取考试列表失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public String toExamDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        Exam exam = examService.getExamById(id);
        if (exam == null) {
            return "redirect:/exam/list";
        }
        model.addAttribute("exam", exam);
        Paper paper = paperService.getPaperById(exam.getPaperId());
        model.addAttribute("paper", paper);
        List<Question> questions = paper != null ? questionMapper.getQuestionsByPaperId(paper.getId()) : java.util.Collections.emptyList();
        model.addAttribute("questions", questions);
        List<PaperQuestion> pqs = paper != null ? paperQuestionMapper.getByPaperId(paper.getId()) : java.util.Collections.emptyList();
        Map<Integer, Integer> scores = new HashMap<>();
        int totalScore = 0;
        for (PaperQuestion pq : pqs) {
            Integer sc = pq.getScore() == null ? 0 : pq.getScore();
            scores.put(pq.getQuestionId(), sc);
            totalScore += sc;
        }
        model.addAttribute("scores", scores);
        model.addAttribute("totalScore", totalScore);
        return "exam/detail";
    }

    @RequestMapping(value = "/submissions/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getExamSubmissions(@PathVariable("id") Integer examId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Exam exam = examService.getExamById(examId);
            if (exam == null) {
                throw new RuntimeException("考试不存在");
            }
            List<Question> qs = questionMapper.getQuestionsByPaperId(exam.getPaperId());
            int totalCount = qs != null ? qs.size() : 0;
            List<ExamSubmission> submissions = examSubmissionService.getByExamId(examId);
            List<Map<String, Object>> data = new ArrayList<>();
            ObjectMapper om = new ObjectMapper();
            for (ExamSubmission sub : submissions) {
                Map<String, Object> item = new HashMap<>();
                item.put("studentId", sub.getStudentId());
                item.put("studentName", sub.getStudentName());
                item.put("status", sub.getStatus());
                item.put("submissionTime", sub.getSubmissionTime());
                item.put("score", sub.getScore() == null ? 0 : sub.getScore());
                int correctCount = 0;
                if (sub.getAnswers() != null && totalCount > 0) {
                    try {
                        JsonNode node = om.readTree(sub.getAnswers());
                        for (Question q : qs) {
                            JsonNode ans = node.get(String.valueOf(q.getId()));
                            if (ans != null && ans.isInt() && ans.asInt() == q.getAnswer()) {
                                correctCount++;
                            }
                        }
                    } catch (Exception ignore) {}
                }
                item.put("correctCount", correctCount);
                item.put("totalCount", totalCount);
                data.add(item);
            }
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取提交情况失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/submissionDetail/{examId}/{studentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSubmissionDetail(@PathVariable("examId") Integer examId,
                                                   @PathVariable("studentId") Integer studentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Exam exam = examService.getExamById(examId);
            if (exam == null) throw new RuntimeException("考试不存在");
            ExamSubmission sub = examSubmissionService.getByExamAndStudent(examId, studentId);
            if (sub == null) throw new RuntimeException("未找到该学生的提交记录");

            List<Question> qs = questionMapper.getQuestionsByPaperId(exam.getPaperId());
            List<PaperQuestion> pqs = paperQuestionMapper.getByPaperId(exam.getPaperId());
            Map<Integer, Integer> scoreMap = new HashMap<>();
            for (PaperQuestion pq : pqs) {
                scoreMap.put(pq.getQuestionId(), pq.getScore() == null ? 0 : pq.getScore());
            }

            ObjectMapper om = new ObjectMapper();
            JsonNode answers = null;
            if (sub.getAnswers() != null && !sub.getAnswers().isEmpty()) {
                try { answers = om.readTree(sub.getAnswers()); } catch (Exception ignore) {}
            }

            int correctCount = 0;
            int totalScore = 0;
            List<Map<String, Object>> details = new ArrayList<>();
            for (Question q : qs) {
                int questionId = q.getId();
                Integer studentAns = null;
                boolean correct = false;
                if (answers != null) {
                    JsonNode node = answers.get(String.valueOf(questionId));
                    if (node != null && node.isInt()) {
                        studentAns = node.asInt();
                        correct = (studentAns == q.getAnswer());
                    }
                }
                int scoreGot = correct ? scoreMap.getOrDefault(questionId, 0) : 0;
                if (correct) correctCount++;
                totalScore += scoreGot;
                Map<String, Object> item = new HashMap<>();
                item.put("questionId", questionId);
                item.put("question", q.getQuestion());
                item.put("correctAnswer", q.getAnswer());
                item.put("studentAnswer", studentAns);
                item.put("correct", correct);
                item.put("score", scoreMap.getOrDefault(questionId, 0));
                item.put("scoreGot", scoreGot);
                details.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("examId", exam.getId());
            data.put("examTitle", exam.getTitle());
            data.put("studentId", sub.getStudentId());
            data.put("studentName", sub.getStudentName());
            data.put("status", sub.getStatus());
            data.put("submissionTime", sub.getSubmissionTime());
            data.put("totalCount", qs != null ? qs.size() : 0);
            data.put("correctCount", correctCount);
            data.put("totalScore", totalScore);
            data.put("details", details);

            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取提交详情失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addExam(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String title = (String) requestData.get("title");
            String description = (String) requestData.get("description");
            String startTime = (String) requestData.get("startTime");
            String endTime = (String) requestData.get("endTime");
            String source = (String) requestData.get("paperSource");

            Integer paperId = null;
            if ("select".equals(source)) {
                Object pid = requestData.get("paperId");
                if (pid == null) throw new RuntimeException("请选择试卷");
                paperId = Integer.parseInt(pid.toString());
            } else if ("auto".equals(source)) {
                Integer count = Integer.parseInt(requestData.get("autoCount").toString());
                String operator = (String) requestData.get("autoOperator");

                List<Question> pool;
                if (operator != null && !operator.isEmpty() && !"all".equals(operator) && !"mixed".equals(operator)) {
                    pool = questionMapper.getQuestionsByOperator(operator);
                } else {
                    pool = questionMapper.getAllQuestions();
                }
                Collections.shuffle(pool);
                List<Question> selected = new ArrayList<>();
                for (int i = 0; i < Math.min(count, pool.size()); i++) {
                    selected.add(pool.get(i));
                }
                if (selected.isEmpty()) throw new RuntimeException("题库中无符合条件的试题");

                Paper paper = new Paper();
                paper.setName("自动组卷_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                paper.setDescription("根据运算符自动组卷");
                paper.setTotalQuestions(selected.size());
                paperService.addPaper(paper);
                if (paper.getId() == null) throw new RuntimeException("试卷创建失败");
                List<PaperQuestion> links = new ArrayList<>();
                int n = selected.size();
                int base = n > 0 ? 100 / n : 0;
                int rem = n > 0 ? 100 % n : 0;
                for (int i = 0; i < n; i++) {
                    Question q = selected.get(i);
                    PaperQuestion pq = new PaperQuestion();
                    pq.setPaperId(paper.getId());
                    pq.setQuestionId(q.getId());
                    pq.setScore(base + (i < rem ? 1 : 0));
                    links.add(pq);
                }
                if (!links.isEmpty()) {
                    paperQuestionMapper.batchInsert(links);
                }
                paperId = paper.getId();
            } else {
                throw new RuntimeException("无效的试卷来源");
            }

            Exam exam = new Exam();
            exam.setTitle(title);
            exam.setDescription(description);
            String dbStart = startTime != null ? (startTime.replace("T", " ") + (startTime.length()==16?":00":"")) : null;
            String dbEnd = endTime != null ? (endTime.replace("T", " ") + (endTime.length()==16?":00":"")) : null;
            exam.setStartTime(dbStart);
            exam.setEndTime(dbEnd);
            exam.setPaperId(paperId);

            SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date s;
            Date e;
            try { s = fmt1.parse(startTime); } catch (Exception pe) { s = fmt2.parse(dbStart); }
            try { e = fmt1.parse(endTime); } catch (Exception pe) { e = fmt2.parse(dbEnd); }
            if (now.before(s)) {
                exam.setStatus("未开始");
            } else if (!now.after(e)) {
                exam.setStatus("进行中");
            } else {
                exam.setStatus("已结束");
            }

            boolean ok = examService.addExam(exam);
            if (!ok || exam.getId() == null) throw new RuntimeException("考试保存失败");

            // 为所有学生创建考试提交记录（pending）
            List<Student> students = studentMapper.findAll();
            for (Student stu : students) {
                ExamSubmission sub = new ExamSubmission();
                sub.setExamId(exam.getId());
                sub.setStudentId(stu.getId());
                sub.setStudentName(stu.getName());
                sub.setSubmissionTime(null);
                sub.setScore(0);
                sub.setStatus("pending");
                sub.setAnswers("{}");
                examSubmissionService.insert(sub);
            }

            result.put("success", true);
            result.put("message", "考试发布成功");
            result.put("url", "/exam/list");
        } catch (Exception ex) {
            result.put("success", false);
            result.put("message", "考试发布失败：" + ex.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteExam(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean ok = examService.deleteExam(id);
            result.put("success", ok);
            result.put("message", ok ? "考试删除成功" : "考试删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "考试删除失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateExam(@RequestBody Exam exam) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (exam.getId() == null) {
                throw new RuntimeException("考试ID不能为空");
            }

            Exam existing = examService.getExamById(exam.getId());
            if (existing == null) {
                throw new RuntimeException("考试不存在");
            }

            boolean allowAll = "未开始".equals(existing.getStatus());

            Exam toUpdate = new Exam();
            toUpdate.setId(existing.getId());
            toUpdate.setTitle(allowAll && exam.getTitle() != null ? exam.getTitle() : existing.getTitle());
            toUpdate.setDescription(allowAll && exam.getDescription() != null ? exam.getDescription() : existing.getDescription());
            toUpdate.setPaperId(allowAll && exam.getPaperId() != null ? exam.getPaperId() : existing.getPaperId());

            String startRaw = allowAll && exam.getStartTime() != null ? exam.getStartTime() : existing.getStartTime();
            String endRaw = exam.getEndTime() != null ? exam.getEndTime() : existing.getEndTime();

            String normStart = startRaw;
            String normEnd = endRaw;
            if (normStart != null) {
                normStart = normStart.replace("T", " ");
                if (normStart.length() == 16) normStart = normStart + ":00";
            }
            if (normEnd != null) {
                normEnd = normEnd.replace("T", " ");
                if (normEnd.length() == 16) normEnd = normEnd + ":00";
            }
            toUpdate.setStartTime(normStart);
            toUpdate.setEndTime(normEnd);

            SimpleDateFormat fmtIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat fmtDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date s;
            Date e;
            try { s = fmtIso.parse(startRaw); } catch (Exception pe) { s = fmtDb.parse(normStart); }
            try { e = fmtIso.parse(endRaw); } catch (Exception pe) { e = fmtDb.parse(normEnd); }

            if (now.before(s)) {
                toUpdate.setStatus("未开始");
            } else if (!now.after(e)) {
                toUpdate.setStatus("进行中");
            } else {
                toUpdate.setStatus("已结束");
            }

            boolean ok = examService.updateExam(toUpdate);
            result.put("success", ok);
            result.put("message", ok ? "考试更新成功" : "考试更新失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "考试更新失败：" + e.getMessage());
        }
        return result;
    }
}