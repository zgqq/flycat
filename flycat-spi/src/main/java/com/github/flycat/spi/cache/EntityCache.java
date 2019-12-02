package com.github.flycat.spi.cache;

import com.github.flycat.util.page.Page;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class EntityCache<T> {

    private final Class<T> entityClass;

    public EntityCache(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public String listPageKey() {
        return "query" + entityClass.getName() + "ListPage";
    }

    public String byIdKey() {
        return "query" + entityClass.getName() + "ById";
    }

    public void removeQueryListPageCache(CacheOperation cacheOperation) {
        cacheOperation.removeCache(listPageKey());
    }

    public void removeQueryByIdCache(CacheOperation cacheOperation, String id) {
        cacheOperation.removeCache(byIdKey(), id);
    }

    public void removeQueryEntityAndPageCache(CacheOperation cacheOperation, String id) {
        removeQueryListPageCache(cacheOperation);
        removeQueryByIdCache(cacheOperation, id);
    }

    public Page<T> queryListPage(
            CacheOperation cacheOperation,
            Callable<Page<T>> listQuery,
            Function<String, List<Map<String, Integer>>> countQuery,
            Integer pageNum,
            String... countColumns) {
        return CommonBusinessUtils.queryListPage(cacheOperation, entityClass, listQuery,
                countQuery
                , pageNum, countColumns
        );
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
