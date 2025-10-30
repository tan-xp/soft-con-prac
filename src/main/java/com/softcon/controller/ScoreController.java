package com.softcon.controller;

import com.softcon.entity.Assignment;
import com.softcon.entity.Student;
import com.softcon.entity.Submission;
import com.softcon.service.AssignmentService;
import com.softcon.service.StudentService;
import com.softcon.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩管理控制器
 */
@Controller
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private StudentService studentService;

    /**
     * 跳转到成绩管理首页
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toScoreList(Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }

        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);

        return "score/list";
    }

    /**
     * 获取所有学生列表（用于成绩管理）
     */
    @RequestMapping(value = "/getAllStudents", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllStudents() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> studentsList = new ArrayList<>();

        try {
            // 获取所有学生信息
            List<Student> students = studentService.getAllStudents();

            for (Student student : students) {
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("id", student.getId());
                studentData.put("studentId", student.getStudentId());
                studentData.put("name", student.getName());
                studentData.put("classInfo", student.getClassInfo());
                studentData.put("gender", student.getGender());
                studentData.put("email", student.getEmail());
                studentData.put("phone", student.getPhone());
                
                // 计算该学生的平均成绩
                double averageScore = calculateStudentAverageScore(student.getId());
                studentData.put("averageScore", averageScore);
                
                // 统计该学生的作业提交情况
                int totalAssignments = assignmentService.getAllAssignments().size();
                List<Submission> submissions = submissionService.getSubmissionsByStudentId(student.getId());
                // 只计算状态为"已提交"的提交记录
                int submittedAssignments = 0;
                if (submissions != null) {
                    submittedAssignments = (int) submissions.stream()
                            .filter(s -> "已提交".equals(s.getStatus()))
                            .count();
                }
                studentData.put("submissionRate", totalAssignments > 0 ? (submittedAssignments * 100.0 / totalAssignments) : 0);
                
                studentsList.add(studentData);
            }

            result.put("success", true);
            result.put("data", studentsList);
            result.put("message", "获取学生列表成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取学生列表失败：" + e.getMessage());
        }

        return result;
    }
    
    /**
     * 获取单个学生的作业成绩详情
     */
    @RequestMapping(value = "/getStudentScores/{studentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getStudentScores(@PathVariable("studentId") Integer studentId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> scoresList = new ArrayList<>();

        try {
            // 获取学生信息
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生不存在");
                return result;
            }
            
            // 获取所有作业
            List<Assignment> assignments = assignmentService.getAllAssignments();

            for (Assignment assignment : assignments) {
                // 获取该学生对这个作业的提交记录
                Submission submission = submissionService.getSubmissionByStudentAndAssignment(studentId, assignment.getId());
                
                Map<String, Object> scoreData = new HashMap<>();
                scoreData.put("assignmentId", assignment.getId());
                scoreData.put("assignmentName", assignment.getTitle());
                scoreData.put("assignmentSubject", assignment.getSubject());
                scoreData.put("assignmentChapter", assignment.getChapter());
                scoreData.put("deadline", assignment.getDeadline());
                
                if (submission != null && "已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                    scoreData.put("submitted", true);
                    scoreData.put("score", submission.getScore());
                    scoreData.put("submissionTime", submission.getSubmissionTime());
                    
                    // 格式化日期
                    String date = "";
                    if (submission.getSubmissionTime() != null) {
                        date = submission.getSubmissionTime().split(" ")[0]; // 只取日期部分
                    }
                    scoreData.put("date", date);
                    
                    // 计算正确题目数和总题目数
                    int totalCount = assignment.getTotalQuestions() != null ? assignment.getTotalQuestions() : 0;
                    int correctCount = totalCount > 0 ? (submission.getScore() * totalCount) / 100 : 0;
                    scoreData.put("correctCount", correctCount);
                    scoreData.put("totalCount", totalCount);
                } else {
                    scoreData.put("submitted", false);
                    scoreData.put("score", 0);
                    scoreData.put("submissionTime", "未提交");
                    scoreData.put("date", "未提交");
                    scoreData.put("correctCount", 0);
                    scoreData.put("totalCount", assignment.getTotalQuestions() != null ? assignment.getTotalQuestions() : 0);
                }
                
                scoresList.add(scoreData);
            }

            // 按作业ID排序
            scoresList.sort(Comparator.comparing(m -> (Integer) m.get("assignmentId")));
            
            Map<String, Object> data = new HashMap<>();
            data.put("student", student);
            data.put("scores", scoresList);
            
            // 计算统计信息
            double averageScore = calculateStudentAverageScore(studentId);
            int totalAssignments = scoresList.size();
            // 获取所有提交记录，只统计状态为"已提交"的记录
            List<Submission> submissions = submissionService.getSubmissionsByStudentId(studentId);
            int submittedAssignments = 0;
            if (submissions != null) {
                submittedAssignments = (int) submissions.stream()
                        .filter(s -> "已提交".equals(s.getStatus()))
                        .count();
            }
            // 计算及格作业数量，只统计状态为"已提交"且分数>=60的记录
            int passedAssignments = 0;
            if (submissions != null) {
                passedAssignments = (int) submissions.stream()
                        .filter(s -> "已提交".equals(s.getStatus()) && s.getScore() != null && s.getScore() >= 60)
                        .count();
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("averageScore", averageScore);
            stats.put("submissionRate", totalAssignments > 0 ? (submittedAssignments * 100.0 / totalAssignments) : 0);
            stats.put("passRate", submittedAssignments > 0 ? (passedAssignments * 100.0 / submittedAssignments) : 0);
            stats.put("totalAssignments", totalAssignments);
            stats.put("submittedAssignments", submittedAssignments);
            stats.put("passedAssignments", passedAssignments);
            
            data.put("stats", stats);
            
            result.put("success", true);
            result.put("data", data);
            result.put("message", "获取学生成绩详情成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取学生成绩详情失败：" + e.getMessage());
        }

        return result;
    }
    
    /**
     * 计算学生的平均成绩
     */
    private double calculateStudentAverageScore(Integer studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudentId(studentId);
        if (submissions == null || submissions.isEmpty()) {
            return 0;
        }
        
        int totalScore = 0;
        int count = 0;
        
        for (Submission submission : submissions) {
            if ("已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                totalScore += submission.getScore();
                count++;
            }
        }
        
        return count > 0 ? (double) totalScore / count : 0;
    }

    /**
     * 获取单个作业的成绩历史（保留原有功能）
     */
    @RequestMapping(value = "/getAssignmentHistory/{assignmentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAssignmentHistory(@PathVariable("assignmentId") Integer assignmentId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> historyList = new ArrayList<>();

        try {
            // 获取作业信息
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);
            if (assignment == null) {
                result.put("success", false);
                result.put("message", "作业不存在");
                return result;
            }

            // 获取该作业的所有提交记录
            List<Submission> submissions = submissionService.getSubmissionsByAssignmentId(assignmentId);

            for (Submission submission : submissions) {
                if ("已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                    Map<String, Object> historyData = new HashMap<>();
                    historyData.put("id", submission.getId().toString());
                    historyData.put("assignmentId", assignment.getId().toString());
                    historyData.put("assignmentName", assignment.getTitle());
                    historyData.put("score", submission.getScore());
                    
                    // 格式化日期
                    String date = "";
                    if (submission.getSubmissionTime() != null) {
                        date = submission.getSubmissionTime().split(" ")[0];
                    }
                    historyData.put("date", date);
                    
                    int totalCount = assignment.getTotalQuestions() != null ? assignment.getTotalQuestions() : 0;
                    int correctCount = totalCount > 0 ? (submission.getScore() * totalCount) / 100 : 0;
                    historyData.put("correctCount", correctCount);
                    historyData.put("totalCount", totalCount);
                    
                    historyList.add(historyData);
                }
            }

            // 按日期排序
            historyList.sort(Comparator.comparing(m -> (String) m.get("date")));

            result.put("success", true);
            result.put("data", historyList);
            result.put("message", "获取作业成绩历史成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取作业成绩历史失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取成绩报表数据
     */
    @RequestMapping(value = "/getReport", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getReport() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取所有作业
            List<Assignment> assignments = assignmentService.getAllAssignments();
            Map<String, Integer> assignmentScores = new HashMap<>();
            Map<String, Integer> assignmentCounts = new HashMap<>();
            
            int totalScore = 0;
            int totalCount = 0;

            for (Assignment assignment : assignments) {
                List<Submission> submissions = submissionService.getSubmissionsByAssignmentId(assignment.getId());
                
                for (Submission submission : submissions) {
                    if ("已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                        // 累计每个作业的总分和提交次数
                        assignmentScores.put(assignment.getTitle(), 
                                assignmentScores.getOrDefault(assignment.getTitle(), 0) + submission.getScore());
                        assignmentCounts.put(assignment.getTitle(), 
                                assignmentCounts.getOrDefault(assignment.getTitle(), 0) + 1);
                        
                        totalScore += submission.getScore();
                        totalCount++;
                    }
                }
            }

            // 计算平均分等统计信息
            Map<String, Object> stats = new HashMap<>();
            stats.put("averageScore", totalCount > 0 ? totalScore / totalCount : 0);
            stats.put("totalAssignments", assignments.size());
            stats.put("totalSubmissions", totalCount);
            
            // 计算每个作业的平均分
            Map<String, Object> assignmentAverages = new HashMap<>();
            for (Map.Entry<String, Integer> entry : assignmentScores.entrySet()) {
                String assignmentName = entry.getKey();
                int scoreSum = entry.getValue();
                int count = assignmentCounts.getOrDefault(assignmentName, 1);
                assignmentAverages.put(assignmentName, scoreSum / count);
            }
            
            stats.put("assignmentAverages", assignmentAverages);

            result.put("success", true);
            result.put("data", stats);
            result.put("message", "获取成绩报表数据成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取成绩报表数据失败：" + e.getMessage());
        }

        return result;
    }
}