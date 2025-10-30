package com.softcon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.softcon.entity.Question;
import com.softcon.entity.Student;
import com.softcon.entity.Submission;
import com.softcon.mapper.QuestionMapper;
import com.softcon.mapper.StudentMapper;
import com.softcon.mapper.SubmissionMapper;
import com.softcon.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 作业提交服务实现类
 */
@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Submission> getSubmissionsByAssignmentId(Integer assignmentId) {
        return submissionMapper.getSubmissionsByAssignmentId(assignmentId);
    }
    
    @Override
    public List<Submission> getSubmissionsByStudentId(Integer studentId) {
        return submissionMapper.getSubmissionsByStudentId(studentId);
    }

    @Override
    public Submission getSubmissionByStudentAndAssignment(Integer studentId, Integer assignmentId) {
        return submissionMapper.getSubmissionByStudentAndAssignment(studentId, assignmentId);
    }

    @Override
    public Map<String, Object> submitAssignment(Integer studentId, Integer assignmentId, String answers, Integer timeSpent) {
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            // 获取学生信息
            Student student = studentMapper.findById(studentId);
            if (student == null) {
                result.put("success", false);
                result.put("message", "学生不存在");
                return result;
            }
            
            // 获取作业题目
            List<Question> questions = questionMapper.getQuestionsByAssignmentId(assignmentId);
            if (questions.isEmpty()) {
                result.put("success", false);
                result.put("message", "作业不存在或没有题目");
                return result;
            }
            
            // 解析学生答案
            JsonNode answersJson = objectMapper.readTree(answers);
            
            // 评分和统计
            int totalQuestions = questions.size();
            int correctCount = 0;
            ArrayNode wrongQuestions = objectMapper.createArrayNode();
            
            // 遍历所有题目，检查答案
            for (Question question : questions) {
                JsonNode answerNode = answersJson.get(String.valueOf(question.getId()));
                if (answerNode != null && !answerNode.isNull()) {
                    Integer userAnswer = answerNode.asInt();
                    if (userAnswer.equals(question.getAnswer())) {
                        correctCount++;
                    } else {
                        // 记录错题
                        ObjectNode wrongQuestionNode = objectMapper.createObjectNode();
                        wrongQuestionNode.put("id", question.getId());
                        wrongQuestionNode.put("question", question.getQuestion());
                        wrongQuestionNode.put("userAnswer", userAnswer);
                        wrongQuestionNode.put("correctAnswer", question.getAnswer());
                        wrongQuestions.add(wrongQuestionNode);
                    }
                }
            }
            
            // 计算分数
            int score = totalQuestions > 0 ? (correctCount * 100) / totalQuestions : 0;
            
            // 获取当前时间
            String submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            // 创建提交记录
            Submission submission = new Submission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(studentId);
            submission.setStudentName(student.getName());
            submission.setSubmissionTime(submitTime);
            submission.setScore(score);
            submission.setStatus("已提交");
            submission.setAnswers(answers);
            
            // 检查是否已存在提交记录，如果存在则更新，不存在则插入
            Submission existingSubmission = submissionMapper.getSubmissionByStudentAndAssignment(studentId, assignmentId);
            if (existingSubmission != null) {
                submission.setId(existingSubmission.getId());
                submissionMapper.update(submission);
            } else {
                submissionMapper.insert(submission);
            }
            
            // 构建返回数据
            Map<String, Object> dataNode = new HashMap<>();
            dataNode.put("score", score);
            dataNode.put("correctCount", correctCount);
            dataNode.put("totalCount", totalQuestions);
            dataNode.put("timeSpent", timeSpent != null ? timeSpent : 0);
            // 转换ArrayNode为List
            List<Integer> wrongQuestionsList = new ArrayList<>();
            if (wrongQuestions != null) {
                for (JsonNode node : wrongQuestions) {
                    wrongQuestionsList.add(node.asInt());
                }
            }
            dataNode.put("wrongQuestions", wrongQuestionsList);
            dataNode.put("submitTime", submitTime);
            
            // 构建返回消息
            result.put("success", true);
            result.put("message", correctCount == totalQuestions ? "恭喜！全部答对！" : "作业提交成功");
            result.put("data", dataNode);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "作业提交失败：" + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public boolean updateSubmission(Submission submission) {
        return submissionMapper.update(submission) > 0;
    }

    @Override
    public boolean deleteSubmissionsByAssignmentId(Integer assignmentId) {
        return submissionMapper.deleteByAssignmentId(assignmentId) >= 0;
    }
}