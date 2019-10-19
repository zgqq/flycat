package com.github.flycat.template.blog.module.post;

import lombok.Data;

@Data
public class Post {
    private String id;
    private String title;
    private String content;
    private String ctime;
    private String flag;
    private Integer uid;

}
