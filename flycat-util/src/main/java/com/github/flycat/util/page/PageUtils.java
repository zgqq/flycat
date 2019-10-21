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
package com.github.flycat.util.page;

import java.util.List;

/**
 * Created by zgq on 17-4-7.
 */
public final class PageUtils {

    private PageUtils() {
    }

    public static <T> Page<T> paginate(PageQuery pageQuery,
                                       Queryer<T> queryer) {
        return paginate(pageQuery.getPageNum(),
                pageQuery.getPageSize(), queryer);
    }


    public static <T> Page<T> paginate(Integer pageNum, Integer pageSize,
                                       Queryer<T> queryer) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        int pageStart = (pageNum - 1) * pageSize;
        int pageLimit = pageSize + 1;
        List<T> query = queryer.query(pageStart, pageLimit);
        short hasMore = hasNext(pageSize, query);
        final Page<T> tPage = new Page<>();
        tPage.setList(query);
        tPage.setHasNext((int) hasMore);
        tPage.setCurrent(pageNum);
        return tPage;
    }

    public static short hasNext(int pageSize, List<?> collection) {
        short hasMore = 1;
        if ((pageSize + 1) > collection.size()) {
            hasMore = 0;
        } else {
            collection.remove(collection.size() - 1);
        }
        return hasMore;
    }
}
