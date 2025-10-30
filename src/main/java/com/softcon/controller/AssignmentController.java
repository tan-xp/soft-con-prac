package com.softcon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softcon.entity.Assignment;
import com.softcon.entity.Question;
import com.softcon.entity.Student;
import com.softcon.entity.Submission;
import com.softcon.mapper.QuestionMapper;
import com.softcon.mapper.StudentMapper;
import com.softcon.mapper.SubmissionMapper;
import com.softcon.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

/**
 * 作业管理控制器
 */
@Controller
@RequestMapping("/assignment")
public class AssignmentController {
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private SubmissionMapper submissionMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    /**
     * 跳转到作业列表页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toAssignmentList(Model model, HttpSession session, @RequestParam(value = "keyword", required = false) String keyword) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        
        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);
        
        // 从数据库获取作业列表
        List<Assignment> assignments = assignmentService.getAllAssignments();
        
        // 如果有搜索关键词，则进行过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowercaseKeyword = keyword.toLowerCase();
            assignments = assignments.stream()
                .filter(assignment -> 
                    assignment.getTitle().toLowerCase().contains(lowercaseKeyword) || 
                    assignment.getDescription().toLowerCase().contains(lowercaseKeyword)
                )
                .collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("assignments", assignments);
        
        return "assignment/list";
    }
    
    /**
     * 跳转到添加作业页面
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toAddAssignment(Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        
        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);
        
        return "assignment/add";
    }
    
    /**
     * 获取所有作业信息
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllAssignments() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            result.put("success", true);
            result.put("data", assignments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取作业列表失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取未分配的试题列表
     */
    @RequestMapping(value = "/getUnassignedQuestions", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUnassignedQuestions() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Question> questions = questionMapper.getUnassignedQuestions();
            result.put("success", true);
            result.put("data", questions);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取未分配试题失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据运算符类型获取未分配的试题
     */
    @RequestMapping(value = "/getUnassignedQuestionsByOperator", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUnassignedQuestionsByOperator(@RequestParam("operator") String operator) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Question> questions = questionMapper.getUnassignedQuestionsByOperator(operator);
            result.put("success", true);
            result.put("data", questions);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取未分配试题失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 随机组卷 - 从数据库中随机选择指定数量的未分配试题
     */
    @RequestMapping(value = "/randomSelectQuestions", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> randomSelectQuestions(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer count = Integer.parseInt(requestData.get("count").toString());
            String operator = (String) requestData.get("operator");
            
            List<Question> allQuestions;
            if (operator != null && !operator.isEmpty() && !"all".equals(operator)) {
                allQuestions = questionMapper.getUnassignedQuestionsByOperator(operator);
            } else {
                allQuestions = questionMapper.getUnassignedQuestions();
            }
            
            // 随机打乱并选择指定数量的试题
            Collections.shuffle(allQuestions);
            List<Question> selectedQuestions = new ArrayList<>();
            for (int i = 0; i < Math.min(count, allQuestions.size()); i++) {
                selectedQuestions.add(allQuestions.get(i));
            }
            
            result.put("success", true);
            result.put("data", selectedQuestions);
            result.put("message", "成功随机选择" + selectedQuestions.size() + "道试题");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "随机组卷失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 跳转到作业详情页面
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public String toAssignmentDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        // 检查登录状态
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        
        // 将teacher对象添加到Model中，供页面使用
        model.addAttribute("teacher", teacher);
        
        // 从数据库获取作业详情
        Assignment assignment = assignmentService.getAssignmentById(id);
        model.addAttribute("assignment", assignment);
        
        // 从数据库获取该作业的题目列表
        List<Question> questions = questionMapper.getQuestionsByAssignmentId(id);
        model.addAttribute("questions", questions);
        
        // 从数据库获取提交记录
        List<Submission> submissions = submissionMapper.getSubmissionsByAssignmentId(id);
        model.addAttribute("submissions", submissions);
        
        return "assignment/detail";
    }
    
    /**
     * 添加作业
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAssignment(@RequestBody Map<String, Object> requestData, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建作业对象
            Assignment assignment = new Assignment();
            assignment.setTitle((String) requestData.get("title"));
            assignment.setDescription((String) requestData.get("description"));
            assignment.setTotalQuestions(Integer.parseInt(requestData.get("totalQuestions").toString()));
            assignment.setDeadline((String) requestData.get("deadline"));
            assignment.setDifficulty((String) requestData.get("difficulty"));
            assignment.setTeacherName((String) requestData.get("teacherName"));
            assignment.setSubject((String) requestData.get("subject"));
            assignment.setChapter((String) requestData.get("chapter"));
            assignment.setIsCompleted(false);
            assignment.setCreatedAt(new Date().toString());
            
            // 保存作业信息
            boolean saveSuccess = assignmentService.addAssignment(assignment);
            
            // 检查作业是否保存成功，以及ID是否正确回填
            if (!saveSuccess || assignment.getId() == null) {
                throw new Exception("作业保存失败或ID未生成：" + (assignment.getId() == null ? "ID为null" : "ID: " + assignment.getId()));
            }
            
            // 获取保存后的作业ID
            Integer assignmentId = assignment.getId();
            System.out.println("保存的作业ID: " + assignmentId);
            
            List<Map<String, Object>> questionsData = (List<Map<String, Object>>) requestData.get("questions");
            if (questionsData != null && !questionsData.isEmpty()) {
                List<Question> newQuestions = new ArrayList<>();
                List<Integer> existingQuestionIds = new ArrayList<>();
                
                for (Map<String, Object> qData : questionsData) {
                    // 检查是否有ID字段，表示是从数据库选择的已有试题
                    Object idObj = qData.get("id");
                    if (idObj != null) {
                        try {
                            Integer questionId = Integer.parseInt(idObj.toString());
                            if (questionId > 0) {
                                // 已有题目，记录ID以便后续关联
                                existingQuestionIds.add(questionId);
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            // ID格式错误，视为新题目处理
                        }
                    }
                    
                    // 新题目，准备插入
                    Question question = new Question();
                    question.setAssignmentId(assignmentId);
                    question.setQuestion((String) qData.get("question"));
                    question.setAnswer(Integer.parseInt(qData.get("answer").toString()));
                    question.setOperator((String) qData.get("operator"));
                    System.out.println("创建新题目，assignmentId: " + question.getAssignmentId());
                    newQuestions.add(question);
                }
                
                // 批量插入新题目
                if (!newQuestions.isEmpty()) {
                    int insertResult = questionMapper.batchInsert(newQuestions);
                    System.out.println("批量插入新题目结果: " + insertResult + "条");
                }
                
                // 关联已有题目（更新assignment_id）
                if (!existingQuestionIds.isEmpty()) {
                    // 检查是否存在batchUpdateAssignmentId方法
                    try {
                        // 尝试调用方法更新已有试题的作业ID
                        questionMapper.batchUpdateAssignmentId(existingQuestionIds, assignmentId);
                        System.out.println("成功关联 " + existingQuestionIds.size() + " 道已有试题");
                    } catch (NoSuchMethodError e) {
                        // 如果方法不存在，则逐个更新
                        System.out.println("批量更新方法不存在，尝试逐个更新");
                        for (Integer questionId : existingQuestionIds) {
                            questionMapper.updateAssignmentId(questionId, assignmentId);
                        }
                    }
                }
            }
            
            // 为所有学生创建提交记录
            List<Student> allStudents = studentMapper.findAll();
            for (Student student : allStudents) {
                // 创建提交记录对象
                Submission submission = new Submission();
                submission.setAssignmentId(assignmentId);
                submission.setStudentId(student.getId());
                submission.setStudentName(student.getName());
                submission.setStatus("未提交");
                submission.setAnswers("{}"); // 空答案JSON
                submission.setScore(0);
                submission.setSubmissionTime(null);
                
                // 插入提交记录
                submissionMapper.insert(submission);
            }
            System.out.println("为 " + allStudents.size() + " 个学生创建了作业提交记录");
            
            result.put("success", true);
            result.put("message", "作业添加成功，已为所有学生创建提交记录");
            result.put("url", "/assignment/list");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "作业添加失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 自动批改作业
     */
    @RequestMapping(value = "/autoGrade/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> autoGradeAssignment(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取作业的所有题目
            List<Question> questions = questionMapper.getQuestionsByAssignmentId(id);
            if (questions.isEmpty()) {
                result.put("success", false);
                result.put("message", "该作业没有题目，无法批改");
                return result;
            }
            
            // 获取所有学生的提交记录
            List<Submission> submissions = submissionMapper.getSubmissionsByAssignmentId(id);
            
            // 自动批改逻辑
            ObjectMapper objectMapper = new ObjectMapper();
            int totalQuestions = questions.size();
            int scorePerQuestion = totalQuestions > 0 ? 100 / totalQuestions : 0;
            
            for (Submission submission : submissions) {
                // 使用数据库中存储的英文状态值进行比较
                if (submission.getStatus() != null && submission.getStatus().equals("已提交") && submission.getAnswers() != null) {
                    try {
                        // 解析学生答案JSON
                        JsonNode answersJson = objectMapper.readTree(submission.getAnswers());
                        int correctCount = 0;
                        
                        // 比对每个问题的答案
                        for (Question question : questions) {
                            // 假设answersJson中以问题ID为key存储答案
                            JsonNode studentAnswer = answersJson.get(String.valueOf(question.getId()));
                            if (studentAnswer != null && studentAnswer.isInt()) {
                                if (studentAnswer.asInt() == question.getAnswer()) {
                                    correctCount++;
                                }
                            }
                        }
                        
                        // 计算分数
                        int score = correctCount * scorePerQuestion;
                        submission.setScore(score);
                        // 由于我们在Mapper中已经实现了状态值转换，这里直接使用中文状态值
                        submission.setStatus("已提交");
                        submissionMapper.update(submission);
                    } catch (Exception e) {
                        // 解析JSON失败时记录错误并跳过该提交
                        e.printStackTrace();
                    }
                }
            }
            
            // 更新作业状态为已完成
            Assignment assignment = assignmentService.getAssignmentById(id);
            assignment.setIsCompleted(true);
            assignmentService.updateAssignment(assignment);
            
            result.put("success", true);
            result.put("message", "自动批改完成");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "自动批改失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除作业
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteAssignment(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 重置该作业的所有题目为未分配状态（将assignment_id设为null）
            questionMapper.resetAssignmentId(id);
            
            // 再删除该作业的所有提交记录
            submissionMapper.deleteByAssignmentId(id);
            
            // 最后删除作业本身
            boolean success = assignmentService.deleteAssignment(id);
            
            if (success) {
                result.put("success", true);
                result.put("message", "作业删除成功");
            } else {
                result.put("success", false);
                result.put("message", "作业删除失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "作业删除失败：" + e.getMessage());
        }
        
        return result;
    }

}