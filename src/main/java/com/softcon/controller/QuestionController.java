package com.softcon.controller;

import com.softcon.entity.Question;
import com.softcon.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 试题管理控制器
 */
@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 跳转到试题管理页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toQuestionList(Model model) {
        // 由于没有获取teacher对象的具体实现，这里暂时添加一个默认值
        // 实际使用时应该从会话或当前登录信息中获取
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("name", "管理员");
        model.addAttribute("teacher", teacherMap);

        // 获取所有试题
        List<Question> questions = questionMapper.getAllQuestions();
        model.addAttribute("questions", questions);

        return "question/list";
    }

    /**
     * 查询试题（支持分页）
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> searchQuestions(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 获取分页数据
            List<Question> questions = questionMapper.searchQuestionsByContentPage(keyword, offset, pageSize);
            
            // 获取总数
            int total = questionMapper.getSearchTotalQuestions(keyword);
            
            // 封装结果
            Map<String, Object> data = new HashMap<>();
            data.put("list", questions);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 生成随机试题
     */
    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> generateQuestions(
            @RequestParam("count") Integer count,
            @RequestParam("operator") String operator,
            @RequestParam("min") Integer min,
            @RequestParam("max") Integer max) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Question> questions = new ArrayList<>();
            Random random = new Random();
            String[] operators = {"+"};
            
            if (operator.equals("-")) {
                operators = new String[]{"-"};
            } else if (operator.equals("mixed")) {
                operators = new String[]{"+", "-"};
            }

            for (int i = 0; i < count; i++) {
                String op = operators[random.nextInt(operators.length)];
                int a, b, answer;
                String questionText;

                if (op.equals("+")) {
                    a = random.nextInt(max - min + 1) + min;
                    b = random.nextInt(max - min + 1) + min;
                    answer = a + b;
                    questionText = a + " + " + b + " = ?";
                } else { // 减法
                    a = random.nextInt(max - min + 1) + min;
                    b = random.nextInt(a - min + 1) + min; // 确保结果非负
                    answer = a - b;
                    questionText = a + " - " + b + " = ?";
                }

                Question question = new Question();
                question.setQuestion(questionText);
                question.setAnswer(answer);
                question.setOperator(op);
                question.setAssignmentId(null); // 临时未分配

                questions.add(question);
            }

            // 现在只生成试题，不直接保存到数据库，等待用户确认后保存
            

            result.put("success", true);
            result.put("message", "成功生成" + count + "道随机试题");
            result.put("data", questions);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成试题失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 手动添加试题
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addQuestion(
            @RequestParam("question") String question,
            @RequestParam("answer") Integer answer,
            @RequestParam("operator") String operator) {
        Map<String, Object> result = new HashMap<>();
        try {
            Question newQuestion = new Question();
            newQuestion.setQuestion(question);
            newQuestion.setAnswer(answer);
            newQuestion.setOperator(operator);
            newQuestion.setAssignmentId(null); // 临时未分配

            questionMapper.insert(newQuestion);
            result.put("success", true);
            result.put("message", "试题添加成功");
            result.put("data", newQuestion);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "试题添加失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除试题
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteQuestion(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            questionMapper.deleteById(id);
            result.put("success", true);
            result.put("message", "试题删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "试题删除失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 测试答案是否正确
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkAnswer(
            @RequestParam("questionId") Integer questionId,
            @RequestParam("answer") Integer answer) {
        Map<String, Object> result = new HashMap<>();
        try {
            Question question = questionMapper.getQuestionById(questionId);
            if (question == null) {
                result.put("success", false);
                result.put("message", "试题不存在");
                return result;
            }

            boolean isCorrect = question.getAnswer().equals(answer);
            result.put("success", true);
            result.put("isCorrect", isCorrect);
            result.put("correctAnswer", question.getAnswer());
            result.put("message", isCorrect ? "答案正确" : "答案错误");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 保存生成的试题到数据库
     */
    @RequestMapping(value = "/saveGenerated", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveGeneratedQuestions(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> questionsData = (List<Map<String, Object>>) requestData.get("questions");
            List<Question> questions = new ArrayList<>();
            
            for (Map<String, Object> questionData : questionsData) {
                Question question = new Question();
                question.setQuestion((String) questionData.get("question"));
                question.setAnswer(((Number) questionData.get("answer")).intValue());
                question.setOperator((String) questionData.get("operator"));
                question.setAssignmentId(null); // 临时未分配
                questions.add(question);
            }
            
            // 批量保存试题
            if (!questions.isEmpty()) {
                questionMapper.batchInsert(questions);
            }
            
            result.put("success", true);
            result.put("message", "成功保存" + questions.size() + "道试题到数据库");
            result.put("data", questions.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存试题失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 刷新试题列表（支持分页）
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> refreshQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 获取分页数据
            List<Question> questions = questionMapper.getAllQuestionsByPage(offset, pageSize);
            
            // 获取总数
            int total = questionMapper.getTotalQuestions();
            
            // 封装结果
            Map<String, Object> data = new HashMap<>();
            data.put("list", questions);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "刷新失败：" + e.getMessage());
        }
        return result;
    }
}