package com.github.bootbox.starter.app.config;


import com.github.bootbox.util.StringReplacer;

public class ConfUtils {
    public static String filterContent(String content) {
        return StringReplacer.replace(AppConf.getContentFilterMap(), content);
    }
}
