package com.github.flycat.template.blog.module.post;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.flycat.template.blog.util.PageUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class PostService {
    @Inject
    private PostMapper postMapper;

    public IPage<Post> getPostListPage(Integer pageNum) {
        return postMapper.selectPage(PageUtils.newPage(pageNum), new QueryWrapper<>());
    }
}
