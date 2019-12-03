package com.github.flycat.web.spring;

public class BaseController {

    public String redirect(String url) {
        return SpringWebUtils.redirect(url);
    }
}
