package com.softcon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*")
public class IndexController {

    @RequestMapping("/")
    public String problem() {
        return "problem";
    }
}
