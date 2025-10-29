package com.softcon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*")
public class IndexController {

    @RequestMapping("/")
    public String index() {
        // 根路径重定向到登录页面
        return "redirect:/login";
    }
}
