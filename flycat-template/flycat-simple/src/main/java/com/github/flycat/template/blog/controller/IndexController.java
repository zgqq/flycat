package com.github.flycat.template.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.flycat.template.blog.module.post.Post;
import com.github.flycat.template.blog.module.post.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        LOGGER.info("Request index page");
        final IPage<Post> postList = postService.getPostListPage(pageNum);
        model.addAttribute("postPage", postList);
        return "index";
    }
}
