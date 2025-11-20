package com.softcon.controller;

import com.softcon.pojo.dto.UserLoginDTO;
import com.softcon.entity.Teacher;
import com.softcon.service.TeacherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 */
@Controller
@CrossOrigin(origins = "*")
@Tag(name = "登录相关接口")
public class LoginController {
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @Operation(summary ="跳转到登录页面")
    public String toLogin() {
        return "login";
    }
    
    /**
     * 处理登录请求
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @Operation(summary ="处理登录请求")
    public Map<String, Object> login(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        // 调用服务层进行登录验证
        Teacher teacher = teacherService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
        
        if (teacher != null) {
            // 登录成功，将用户信息存入session
            session.setAttribute("teacher", teacher);
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("url", "/index");
        } else {
            // 登录失败
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        
        return result;
    }
    
    /**
     * 跳转到系统首页
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @Operation(summary ="跳转到系统首页")
    public String toIndex(Model model, HttpSession session) {
        // 从session中获取教师信息
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        
        if (teacher == null) {
            // 未登录，重定向到登录页面
            return "redirect:/login";
        }
        
        // 将教师信息传递给页面
        model.addAttribute("teacher", teacher);
        return "index";
    }
    
    /**
     * 注销登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @Operation(summary ="注销登录")
    public String logout(HttpSession session) {
        // 清除session中的用户信息
        session.invalidate();
        // 重定向到登录页面
        return "redirect:/login";
    }
}