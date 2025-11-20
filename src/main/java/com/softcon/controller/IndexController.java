package com.softcon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Controller
@CrossOrigin(origins = "*")
@Tag(name = "首页相关接口")
public class IndexController {

    @RequestMapping("/")
    @Operation(summary ="根路径重定向到登录页面")
    public String index() {
        // 根路径重定向到登录页面
        return "redirect:/login";
    }
}
