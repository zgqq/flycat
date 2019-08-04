package com.github.bootbox.util.page;

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
        short hasMore = hasMore(pageSize, query);
        return new Page<T>(query, hasMore);
    }

    public static short hasMore(int pageSize, List<?> collection) {
        short hasMore = 1;
        if ((pageSize + 1) > collection.size()) {
            hasMore = 0;
        } else {
            collection.remove(collection.size() - 1);
        }
        return hasMore;
    }


}
