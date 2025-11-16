package com.softcon.controller;

import com.softcon.entity.Paper;
import com.softcon.entity.PaperQuestion;
import com.softcon.entity.Question;
import com.softcon.mapper.PaperQuestionMapper;
import com.softcon.mapper.QuestionMapper;
import com.softcon.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toPaperList(Model model, HttpSession session) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        model.addAttribute("papers", paperService.getAllPapers());
        return "paper/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toAddPaper(Model model, HttpSession session) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        return "paper/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String toEditPaper(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        Paper paper = paperService.getPaperById(id);
        model.addAttribute("paper", paper);
        return "paper/edit";
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public String toPaperDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Object teacher = session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/login";
        }
        model.addAttribute("teacher", teacher);
        Paper paper = paperService.getPaperById(id);
        model.addAttribute("paper", paper);
        List<Question> questions = questionMapper.getQuestionsByPaperId(id);
        model.addAttribute("questions", questions);
        List<PaperQuestion> pqs = paperQuestionMapper.getByPaperId(id);
        Map<Integer, Integer> scores = new HashMap<>();
        int totalScore = 0;
        for (PaperQuestion pq : pqs) {
            Integer sc = pq.getScore() == null ? 0 : pq.getScore();
            scores.put(pq.getQuestionId(), sc);
            totalScore += sc;
        }
        model.addAttribute("scores", scores);
        model.addAttribute("totalScore", totalScore);
        return "paper/detail";
    }

    @RequestMapping(value = "/detailJson/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getPaperDetail(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Paper paper = paperService.getPaperById(id);
            List<Question> questions = questionMapper.getQuestionsByPaperId(id);
            List<PaperQuestion> pqs = paperQuestionMapper.getByPaperId(id);
            Map<Integer, Integer> scores = new HashMap<>();
            for (PaperQuestion pq : pqs) {
                scores.put(pq.getQuestionId(), pq.getScore() == null ? 0 : pq.getScore());
            }
            Map<String, Object> data = new HashMap<>();
            data.put("paper", paper);
            data.put("questions", questions);
            data.put("scores", scores);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取试卷详情失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updatePaper(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer id = Integer.parseInt(requestData.get("id").toString());
            Paper paper = paperService.getPaperById(id);
            if (paper == null) {
                throw new RuntimeException("试卷不存在");
            }
            String name = (String) requestData.get("name");
            String description = (String) requestData.get("description");
            List<Map<String, Object>> items = (List<Map<String, Object>>) requestData.get("items");
            if (items == null || items.isEmpty()) {
                throw new RuntimeException("请至少选择一道试题");
            }
            int total = 0;
            List<PaperQuestion> links = new ArrayList<>();
            for (Map<String, Object> it : items) {
                Integer qid = Integer.parseInt(it.get("questionId").toString());
                Integer score = Integer.parseInt(it.get("score").toString());
                total += score;
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(id);
                pq.setQuestionId(qid);
                pq.setScore(score);
                links.add(pq);
            }
            if (total != 100) {
                throw new RuntimeException("试卷总分必须为100分，当前为" + total + "分");
            }
            paper.setName(name);
            paper.setDescription(description);
            paper.setTotalQuestions(items.size());
            paperService.updatePaper(paper);
            paperQuestionMapper.deleteByPaperId(id);
            paperQuestionMapper.batchInsert(links);
            result.put("success", true);
            result.put("message", "试卷更新成功");
            result.put("url", "/paper/list");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "试卷更新失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllPapers() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", paperService.getAllPapers());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取试卷列表失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/getQuestions", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getQuestions(@RequestParam(value = "operator", required = false) String operator) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Question> list;
            if (operator != null && !operator.isEmpty() && !"all".equals(operator) && !"mixed".equals(operator)) {
                list = questionMapper.getQuestionsByOperator(operator);
            } else {
                list = questionMapper.getAllQuestions();
            }
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取题库试题失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addPaper(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String name = (String) requestData.get("name");
            String description = (String) requestData.get("description");
            List<Map<String, Object>> items = null;
            Object itemsObj = requestData.get("items");
            if (itemsObj instanceof List<?>) {
                items = (List<Map<String, Object>>) itemsObj;
            }
            List<Integer> fallbackIds = new ArrayList<>();
            Object qObj = requestData.get("questionIds");
            if (items == null && qObj instanceof List<?>) {
                for (Object o : (List<?>) qObj) {
                    fallbackIds.add(Integer.parseInt(o.toString()));
                }
            }
            if ((items == null || items.isEmpty()) && fallbackIds.isEmpty()) {
                throw new RuntimeException("请至少选择一道试题");
            }

            Paper paper = new Paper();
            paper.setName(name);
            paper.setDescription(description);
            paper.setTotalQuestions(items != null ? items.size() : fallbackIds.size());
            paperService.addPaper(paper);
            if (paper.getId() == null) throw new RuntimeException("试卷保存失败");

            List<PaperQuestion> links = new ArrayList<>();
            if (items != null && !items.isEmpty()) {
                int total = 0;
                for (Map<String, Object> it : items) {
                    Integer qid = Integer.parseInt(it.get("questionId").toString());
                    Integer score = Integer.parseInt(it.get("score").toString());
                    total += score;
                    PaperQuestion pq = new PaperQuestion();
                    pq.setPaperId(paper.getId());
                    pq.setQuestionId(qid);
                    pq.setScore(score);
                    links.add(pq);
                }
                if (total != 100) {
                    throw new RuntimeException("试卷总分必须为100分，当前为" + total + "分");
                }
            } else {
                int n = fallbackIds.size();
                int base = n > 0 ? 100 / n : 0;
                int rem = n > 0 ? 100 % n : 0;
                for (int i = 0; i < n; i++) {
                    PaperQuestion pq = new PaperQuestion();
                    pq.setPaperId(paper.getId());
                    pq.setQuestionId(fallbackIds.get(i));
                    pq.setScore(base + (i < rem ? 1 : 0));
                    links.add(pq);
                }
            }
            paperQuestionMapper.batchInsert(links);

            result.put("success", true);
            result.put("message", "试卷创建成功");
            result.put("url", "/paper/list");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "试卷创建失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deletePaper(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            paperQuestionMapper.deleteByPaperId(id);
            boolean ok = paperService.deletePaper(id);
            result.put("success", ok);
            result.put("message", ok ? "试卷删除成功" : "试卷删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "试卷删除失败：" + e.getMessage());
        }
        return result;
    }
}