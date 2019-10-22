package com.github.flycat.db.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.flycat.util.ValueUtils;
import com.github.flycat.util.page.Page;

import java.util.List;

public class PageUtils {

    public static <T> Page<T> toPage(IPage<T> page) {
        final List<T> records = page.getRecords();
        final Page<T> tPage = new Page<>();
        tPage.setList(records);
        tPage.setCurrent((int) page.getCurrent());
        tPage.setTotal(page.getTotal());
        tPage.setTotalPages((int) page.getPages());
        return tPage;
    }

    public static IPage newIPage(Integer pageNum) {
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(ValueUtils.integerToInt(pageNum, 1), 10);
    }
}
