package com.github.flycat.template.blog.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.flycat.util.ValueUtils;

public class PageUtils {

    public static IPage newPage(Integer pageNum) {
        return new Page<>(ValueUtils.integerToInt(pageNum, 1), 10);
    }
}
