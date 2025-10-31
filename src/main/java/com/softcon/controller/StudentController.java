package com.softcon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcon.entity.Assignment;
import com.softcon.entity.Question;
import com.softcon.entity.Student;
import com.softcon.entity.Submission;
import com.softcon.mapper.QuestionMapper;
import com.softcon.mapper.SubmissionMapper;
import com.softcon.service.AssignmentService;
import com.softcon.service.StudentService;
import com.softcon.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.softcon.util.JwtUtil;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生管理控制器
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionMapper submissionMapper;

    /**
     * 获取学生作业列表接口
     */
    @RequestMapping(value = "/assignments", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAssignments(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从请求属性中获取当前登录的学生信息
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }
            
            // 根据学号获取学生ID
            Student student = studentService.getStudentByStudentId(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生信息不存在");
                result.put("data", null);
                return result;
            }
            
            // 获取所有作业信息
            List<Assignment> assignments = assignmentService.getAllAssignments();
            
            // 获取该学生的作业提交记录
            List<Submission> submissions = submissionService.getSubmissionsByStudentId(student.getId());
            
            // 创建提交记录映射，方便查找每个作业的提交状态
            Map<Integer, Submission> submissionMap = new HashMap<>();
            for (Submission submission : submissions) {
                submissionMap.put(submission.getAssignmentId(), submission);
            }
            
            // 构建响应数据
            List<Map<String, Object>> assignmentList = new ArrayList<>();
            for (Assignment assignment : assignments) {
                Map<String, Object> assignmentData = new HashMap<>();
                assignmentData.put("id", assignment.getId().toString());
                assignmentData.put("title", assignment.getTitle());
                assignmentData.put("description", assignment.getDescription());
                assignmentData.put("totalQuestions", assignment.getTotalQuestions());
                assignmentData.put("deadline", assignment.getDeadline());
                
                // 检查是否已完成（根据提交记录）
                boolean isCompleted = false;
                Submission submission = submissionMap.get(assignment.getId());
                if (submission != null && "已提交".equals(submission.getStatus())) {
                    isCompleted = true;
                }
                assignmentData.put("isCompleted", isCompleted);
                
                assignmentData.put("createdAt", assignment.getCreatedAt());
                assignmentData.put("difficulty", assignment.getDifficulty());
                
                assignmentList.add(assignmentData);
            }
            
            result.put("success", true);
            result.put("message", "");
            result.put("data", assignmentList);
            
        
    }catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "获取作业列表失败：" + e.getMessage());
            result.put("data", null);
        }
        
        return result;
    }

    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 学生登录接口
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> studentLogin(@RequestBody Map<String, String> requestData) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取请求参数
            String studentId = requestData.get("studentId");
            String password = requestData.get("password");

            // 参数校验
            if (studentId == null || studentId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "学号和密码不能为空");
                result.put("data", null);
                return result;
            }

            // 调用服务层进行登录验证
            Student student = studentService.login(studentId.trim(), password);

            if (student != null) {
                // 登录成功，生成 JWT Token
                String token = jwtUtil.generateToken(student.getStudentId(), student.getName());

                // 构造返回数据
                Map<String, Object> data = new HashMap<>();
                data.put("id", student.getId().toString());
                data.put("studentId", student.getStudentId());
                data.put("name", student.getName());
                data.put("token", token);  // 返回 Token
                // 提取年级和班级信息（从classInfo字段解析）
                String classInfo = student.getClassInfo();
                data.put("grade", classInfo != null ? classInfo.split("年级")[0] + "年级" : "");
                data.put("class",
                        classInfo != null && classInfo.contains("班")
                                ? classInfo.substring(classInfo.lastIndexOf("年级") + 2)
                                : "");

                result.put("success", true);
                result.put("message", "登录成功");
                result.put("data", data);
            } else {
                // 登录失败
                result.put("success", false);
                result.put("message", "学号或密码错误");
                result.put("data", null);
            }
        } catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "登录失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 跳转到学生列表页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toStudentList(Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }

        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);

        // 获取所有学生信息
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "student/list";
    }

    /**
     * 跳转到添加学生页面
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toAddStudent(Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }

        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);
        return "student/add";
    }

    /**
     * 跳转到编辑学生页面
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String toEditStudent(@PathVariable("id") Integer id, Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }

        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);

        // 获取学生信息
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/student/list";
        }
        model.addAttribute("student", student);
        return "student/edit";
    }

    /**
     * 添加学生信息
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addStudent(@RequestBody Student student) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = studentService.addStudent(student);
            if (success) {
                result.put("success", true);
                result.put("message", "学生添加成功");
                result.put("url", "/student/list");
            } else {
                result.put("success", false);
                result.put("message", "用户名已存在，请更换用户名");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "学生添加失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 更新学生信息
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateStudent(@RequestBody Student student) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = studentService.updateStudent(student);
            if (success) {
                result.put("success", true);
                result.put("message", "学生信息更新成功");
                result.put("url", "/student/list");
            } else {
                result.put("success", false);
                result.put("message", "用户名已存在，请更换用户名");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "学生信息更新失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 删除学生信息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteStudent(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = studentService.deleteStudent(id);
            if (success) {
                result.put("success", true);
                result.put("message", "学生信息删除成功");
            } else {
                result.put("success", false);
                result.put("message", "学生信息删除失败，学生不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "学生信息删除失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 搜索学生信息
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> searchStudents(@RequestParam("keyword") String keyword) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Student> students = studentService.searchStudents(keyword);
            result.put("success", true);
            result.put("students", students);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 重置学生密码
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam("id") Integer id,
            @RequestParam("newPassword") String newPassword) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = studentService.resetPassword(id, newPassword);
            if (success) {
                result.put("success", true);
                result.put("message", "密码重置成功");
            } else {
                result.put("success", false);
                result.put("message", "密码重置失败，学生不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "密码重置失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取学生提交详情
     */
    @RequestMapping(value = "/submission/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSubmissionDetail(@PathVariable("id") Integer submissionId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取提交记录
            Submission submission = submissionMapper.getSubmissionById(submissionId);
            if (submission == null) {
                result.put("success", false);
                result.put("message", "提交记录不存在");
                return result;
            }
            
            // 获取该作业的所有题目
            List<Question> questions = questionMapper.getQuestionsByAssignmentId(submission.getAssignmentId());
            
            // 构建详情数据
            Map<String, Object> detail = new HashMap<>();
            detail.put("submission", submission);
            detail.put("questions", questions);
            
            result.put("success", true);
            result.put("data", detail);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取提交详情失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 学生提交作业
     */
    @RequestMapping(value = "/submitAssignment", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitAssignment(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {

        // 从请求属性中获取当前登录的学生信息
        String studentId = (String) request.getAttribute("studentId");
        if (studentId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        // 根据学号获取学生ID
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "学生信息不存在");
            return result;
        }

        try {
            // 获取请求参数
            Integer assignmentId = (Integer) requestData.get("assignmentId");
            String answers = (String) requestData.get("answers");
            Integer timeSpent = (Integer) requestData.get("timeSpent");

            // 参数校验
            if (assignmentId == null || answers == null || answers.trim().isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }

            // 调用服务层提交作业
            return submissionService.submitAssignment(
                    student.getId(),
                    assignmentId,
                    answers,
                    timeSpent);

        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "作业提交失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取学生的作业列表
     */
    @RequestMapping(value = "/assignment/list", method = RequestMethod.GET)
    public String toStudentAssignmentList(Model model, HttpServletRequest request) {
        // 检查登录状态
        String studentId = (String) request.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }

        // 根据学号获取学生信息
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            return "redirect:/login";
        }

        // 将student对象添加到Model中，供页面使用
        model.addAttribute("student", student);

        return "student/assignment/list";
    }

    /**
     * 获取作业详情（学生端）
     */
    @RequestMapping(value = "/assignment/detail/{id}", method = RequestMethod.GET)
    public String toStudentAssignmentDetail(
            @PathVariable("id") Integer id,
            Model model,
            HttpServletRequest request) {
        // 检查登录状态
        String studentId = (String) request.getAttribute("studentId");
        if (studentId == null) {
            return "redirect:/login";
        }

        // 根据学号获取学生信息
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            return "redirect:/login";
        }

        // 将student对象和作业ID添加到Model中，供页面使用
        model.addAttribute("student", student);
        model.addAttribute("assignmentId", id);

        return "student/assignment/detail";
    }

    /**
     * 获取学生的成绩记录接口
     */
    @RequestMapping(value = "/scores/{assignmentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getScores(
            @PathVariable(value = "assignmentId", required = false) String assignmentIdStr,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求属性中获取当前登录的学生信息
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }

            // 根据学号获取学生ID
            Student student = studentService.getStudentByStudentId(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生信息不存在");
                result.put("data", null);
                return result;
            }

            // 获取该学生的所有提交记录
            List<Submission> submissions = submissionService.getSubmissionsByStudentId(student.getId());

            // 获取所有作业信息，用于匹配作业名称
            List<Assignment> assignments = assignmentService.getAllAssignments();
            Map<Integer, Assignment> assignmentMap = new HashMap<>();
            for (Assignment assignment : assignments) {
                assignmentMap.put(assignment.getId(), assignment);
            }

            // 构建成绩列表数据
            List<Map<String, Object>> scoreList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            for (Submission submission : submissions) {
                // 只处理已提交且有成绩的记录
                if ("已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                    // 如果指定了作业ID，则只处理该作业的记录
                    if (assignmentIdStr != null && !"undefined".equals(assignmentIdStr)) {
                        try {
                            Integer assignmentId = Integer.parseInt(assignmentIdStr);
                            if (!assignmentId.equals(submission.getAssignmentId())) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            result.put("success", false);
                            result.put("message", "无效的作业ID格式");
                            result.put("data", null);
                            return result;
                        }
                    }

                    // 获取作业信息
                    Assignment assignment = assignmentMap.get(submission.getAssignmentId());
                    if (assignment == null) {
                        continue; // 跳过找不到作业信息的记录
                    }

                    Map<String, Object> scoreData = new HashMap<>();
                    scoreData.put("id", submission.getId().toString());
                    scoreData.put("assignmentId", submission.getAssignmentId().toString());
                    scoreData.put("assignmentName", assignment.getTitle());
                    scoreData.put("score", submission.getScore());

                    // 格式化日期（只保留年月日）
                    if (submission.getSubmissionTime() != null && submission.getSubmissionTime().contains(" ")) {
                        scoreData.put("date", submission.getSubmissionTime().split(" ")[0]);
                    } else {
                        scoreData.put("date", submission.getSubmissionTime());
                    }

                    // 解析答案JSON，计算正确数量和错误题目
                    if (submission.getAnswers() != null && !submission.getAnswers().isEmpty()) {
                        try {
                            JsonNode answersJson = objectMapper.readTree(submission.getAnswers());
                            // 获取作业的所有题目，用于判断对错
                            List<Question> questions = questionMapper
                                    .getQuestionsByAssignmentId(submission.getAssignmentId());

                            int correctCount = 0;
                            List<Integer> wrongQuestionIds = new ArrayList<>();

                            for (Question question : questions) {
                                JsonNode answerNode = answersJson.get(String.valueOf(question.getId()));
                                if (answerNode != null && !answerNode.isNull()) {
                                    Integer userAnswer = answerNode.asInt();
                                    if (userAnswer.equals(question.getAnswer())) {
                                        correctCount++;
                                    } else {
                                        wrongQuestionIds.add(question.getId());
                                    }
                                }
                            }

                            scoreData.put("correctCount", correctCount);
                            scoreData.put("totalCount", questions.size());

                            // 添加错题信息（只添加题目ID）
                            if (!wrongQuestionIds.isEmpty()) {
                                scoreData.put("wrongQuestions", wrongQuestionIds);
                            }

                        } catch (Exception e) {
                            // 解析失败时不影响其他数据
                            scoreData.put("correctCount", 0);
                            scoreData.put("totalCount", 0);
                        }
                    }

                    scoreList.add(scoreData);
                }
            }

            result.put("success", true);
            result.put("message", "");
            result.put("data", scoreList);

        } catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "获取成绩记录失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 学生端获取所有成绩接口
     */
    @RequestMapping(value = "/scores", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllScores(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求属性中获取当前登录的学生信息（由JWT拦截器设置）
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }

            // 根据学号获取学生ID
            Student student = studentService.getStudentByStudentId(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生信息不存在");
                result.put("data", null);
                return result;
            }

            // 获取该学生的所有提交记录
            List<Submission> submissions = submissionService.getSubmissionsByStudentId(student.getId());

            // 获取所有作业信息，用于匹配作业名称
            List<Assignment> assignments = assignmentService.getAllAssignments();
            Map<Integer, Assignment> assignmentMap = new HashMap<>();
            for (Assignment assignment : assignments) {
                assignmentMap.put(assignment.getId(), assignment);
            }

            // 构建成绩列表数据
            List<Map<String, Object>> scoreList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            for (Submission submission : submissions) {
                // 只处理已提交且有成绩的记录
                if ("已提交".equals(submission.getStatus()) && submission.getScore() != null) {
                    // 获取作业信息
                    Assignment assignment = assignmentMap.get(submission.getAssignmentId());
                    if (assignment == null) {
                        continue; // 跳过找不到作业信息的记录
                    }

                    Map<String, Object> scoreData = new HashMap<>();
                    // 设置ID字段（转换为字符串）
                    scoreData.put("id", submission.getId().toString());
                    scoreData.put("assignmentId", submission.getAssignmentId().toString());
                    scoreData.put("assignmentName", assignment.getTitle());
                    scoreData.put("score", submission.getScore());

                    // 格式化日期（只保留年月日）
                    if (submission.getSubmissionTime() != null && submission.getSubmissionTime().contains(" ")) {
                        scoreData.put("date", submission.getSubmissionTime().split(" ")[0]);
                    } else {
                        scoreData.put("date", submission.getSubmissionTime());
                    }

                    // 解析答案JSON，计算正确数量和错误题目
                    if (submission.getAnswers() != null && !submission.getAnswers().isEmpty()) {
                        try {
                            JsonNode answersJson = objectMapper.readTree(submission.getAnswers());
                            // 获取作业的所有题目，用于判断对错
                            List<Question> questions = questionMapper
                                    .getQuestionsByAssignmentId(submission.getAssignmentId());

                            int correctCount = 0;
                            List<Integer> wrongQuestionIds = new ArrayList<>();

                            for (Question question : questions) {
                                JsonNode answerNode = answersJson.get(String.valueOf(question.getId()));
                                if (answerNode != null && !answerNode.isNull()) {
                                    Integer userAnswer = answerNode.asInt();
                                    if (userAnswer.equals(question.getAnswer())) {
                                        correctCount++;
                                    } else {
                                        wrongQuestionIds.add(question.getId());
                                    }
                                }
                            }

                            scoreData.put("correctCount", correctCount);
                            scoreData.put("totalCount", questions.size());

                            // 添加错题信息（只添加题目ID）
                            if (!wrongQuestionIds.isEmpty()) {
                                scoreData.put("wrongQuestions", wrongQuestionIds);
                            }

                        } catch (Exception e) {
                            // 解析失败时不影响其他数据
                            scoreData.put("correctCount", 0);
                            scoreData.put("totalCount", 0);
                        }
                    }

                    // 添加完成时间（默认值，实际项目中可能需要从数据库获取）
                    // 注意：这里需要根据实际情况从submission对象获取timeSpent
                    // 如果Submission实体类中没有timeSpent字段，可能需要扩展实体类或通过其他方式获取
                    scoreData.put("timeSpent", 0); // 临时默认值

                    scoreList.add(scoreData);
                }
            }

            result.put("success", true);
            result.put("message", "");
            result.put("data", scoreList);

        } catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "获取成绩记录失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
    
    /**
     * 提交作业答案，获取评分结果
     */
    @RequestMapping(value = "/submit/{assignmentId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitAssignmentWithId(
            @PathVariable("assignmentId") String assignmentIdStr,
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求属性中获取当前登录的学生信息
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }
            
            // 根据学号获取学生ID
            Student student = studentService.getStudentByStudentId(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生信息不存在");
                result.put("data", null);
                return result;
            }

            // 解析作业ID
            Integer assignmentId;
            try {
                assignmentId = Integer.parseInt(assignmentIdStr);
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "无效的作业ID格式");
                result.put("data", null);
                return result;
            }

            // 获取请求参数
            List<Object> answersArray = (List<Object>) requestData.get("answers");
            Integer totalQuestions = (Integer) requestData.get("totalQuestions");
            Integer timeSpent = requestData.get("timeSpent") instanceof Number
                    ? ((Number) requestData.get("timeSpent")).intValue()
                    : null;

            // 参数校验
            if (answersArray == null || answersArray.isEmpty() || totalQuestions == null || totalQuestions <= 0) {
                result.put("success", false);
                result.put("message", "参数不完整");
                result.put("data", null);
                return result;
            }

            // 将answers数组转换为JSON格式
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Integer> answersMap = new HashMap<>();
            for (int i = 0; i < answersArray.size(); i++) {
                // 假设answers数组中的索引对应题目ID
                answersMap.put(String.valueOf(i + 1),
                        answersArray.get(i) instanceof Number ? ((Number) answersArray.get(i)).intValue()
                                : Integer.parseInt(answersArray.get(i).toString()));
            }
            String answersJson = objectMapper.writeValueAsString(answersMap);

            // 调用服务层提交作业
            Map<String, Object> submitResult = submissionService.submitAssignment(
                    student.getId(),
                    assignmentId,
                    answersJson,
                    timeSpent);

            // 处理服务层返回结果
            boolean success = (boolean) submitResult.get("success");
            result.put("success", success);
            result.put("message", submitResult.get("message"));

            if (success) {
                // 构建符合要求的数据格式
                Map<String, Object> dataNode = (Map<String, Object>) submitResult.get("data");
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("score", dataNode.get("score"));
                responseData.put("correctCount", dataNode.get("correctCount"));
                responseData.put("totalCount", dataNode.get("totalCount"));
                result.put("data", responseData);
            } else {
                result.put("data", null);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "作业提交失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 学生端作业完成接口 - 符合Submission实体类设计
     */
    @RequestMapping(value = "/assignment/complete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> completeAssignment(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求属性中获取当前登录的学生信息（由JWT拦截器设置）
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }
            
            // 根据学号获取学生信息
            Student student = studentService.getStudentByStudentId(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生信息不存在");
                result.put("data", null);
                return result;
            }

            // 参数验证 - 基于Submission实体类设计
            Integer assignmentId = null;
            Object assignmentIdObj = requestData.get("assignmentId");
            if (assignmentIdObj != null) {
                if (assignmentIdObj instanceof Number) {
                    assignmentId = ((Number) assignmentIdObj).intValue();
                } else if (assignmentIdObj instanceof String) {
                    try {
                        assignmentId = Integer.parseInt((String) assignmentIdObj);
                    } catch (NumberFormatException e) {
                        // 解析失败，将在下面进行验证
                    }
                }
            }

            String answers = null;
            Object answersObj = requestData.get("answers");
            if (answersObj != null) {
                if (answersObj instanceof String) {
                    answers = (String) answersObj;
                } else {
                    // 如果答案是对象或数组，转换为JSON字符串
                    ObjectMapper objectMapper = new ObjectMapper();
                    answers = objectMapper.writeValueAsString(answersObj);
                }
            }

            // 时间花费（可选参数）
            Integer timeSpent = null;
            Object timeSpentObj = requestData.get("timeSpent");
            if (timeSpentObj instanceof Number) {
                timeSpent = ((Number) timeSpentObj).intValue();
            } else if (timeSpentObj instanceof String) {
                try {
                    timeSpent = Integer.parseInt((String) timeSpentObj);
                } catch (NumberFormatException e) {
                    // 解析失败，使用null
                }
            }

            // 核心参数校验
            if (assignmentId == null || assignmentId <= 0) {
                result.put("success", false);
                result.put("message", "作业ID无效");
                result.put("data", null);
                return result;
            }

            if (answers == null || answers.trim().isEmpty() || answers.equals("{}") || answers.equals("[]")) {
                result.put("success", false);
                result.put("message", "请提交有效的答案");
                result.put("data", null);
                return result;
            }

            // 调用服务层提交作业
            Map<String, Object> submitResult = submissionService.submitAssignment(
                    student.getId(),
                    assignmentId,
                    answers,
                    timeSpent);

            // 处理服务层返回结果
            boolean success = (boolean) submitResult.get("success");
            result.put("success", success);
            result.put("message", submitResult.get("message"));

            if (success) {
                // 构建符合要求的数据格式，包含提交记录ID和成绩信息
                Map<String, Object> responseData = new HashMap<>();
                
                // 获取提交记录ID
                if (submitResult.containsKey("submissionId")) {
                    responseData.put("submissionId", submitResult.get("submissionId"));
                }
                
                // 如果有成绩信息，添加到响应中
                if (submitResult.containsKey("data")) {
                    Map<String, Object> dataNode = (Map<String, Object>) submitResult.get("data");
                    responseData.put("score", dataNode.get("score"));
                    responseData.put("correctCount", dataNode.get("correctCount"));
                    responseData.put("totalCount", dataNode.get("totalCount"));
                }
                
                // 添加提交时间信息
                responseData.put("submissionTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                responseData.put("status", "已提交");
                
                result.put("data", responseData);
            } else {
                result.put("data", null);
            }

        } catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "作业提交失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
    
    /**
     * 获取指定作业的题目列表接口
     */
    @RequestMapping(value = "/questions/{assignmentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getQuestionsByAssignmentId(
            @PathVariable("assignmentId") String assignmentIdStr,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从请求属性中获取当前登录的学生信息（由拦截器设置）
            String studentId = (String) request.getAttribute("studentId");
            if (studentId == null) {
                // 这种情况理论上不会发生，因为拦截器已经验证过
                result.put("success", false);
                result.put("message", "请先登录");
                result.put("data", null);
                return result;
            }

            // 解析作业ID
            Integer assignmentId;
            try {
                assignmentId = Integer.parseInt(assignmentIdStr);
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "无效的作业ID格式");
                result.put("data", null);
                return result;
            }

            // 获取该作业的题目列表
            List<Question> questions = questionMapper.getQuestionsByAssignmentId(assignmentId);

            // 构建响应数据
            List<Map<String, Object>> questionList = new ArrayList<>();
            for (Question question : questions) {
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("id", question.getId());
                questionData.put("question", question.getQuestion());
                questionData.put("answer", question.getAnswer());
                questionData.put("operator", question.getOperator());
                questionList.add(questionData);
            }

            result.put("success", true);
            result.put("message", "");
            result.put("data", questionList);

        } catch (Exception e) {
            // 处理异常
            result.put("success", false);
            result.put("message", "获取题目列表失败：" + e.getMessage());
            result.put("data", null);
        }

        return result;
    }
}