package com.github.flycat.template.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1")
public class StatusController {

    @GetMapping("/status")
    @ResponseBody
    public String status() {
        return "ok";
    }
}
