/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.db.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                ValueUtils.integerToInt(pageNum, 1), 10);
    }

    public static <T> Page<T> selectPage(BaseMapper<T> baseMapper, int finalPageNum, QueryWrapper queryWrapper) {
        return PageUtils.toPage(baseMapper.selectPage(PageUtils.newIPage(finalPageNum), queryWrapper));
    }

    public static <T> Page<T> selectPage(BaseMapper<T> baseMapper, int finalPageNum) {
        return selectPage(baseMapper, finalPageNum, new QueryWrapper());
    }
}
