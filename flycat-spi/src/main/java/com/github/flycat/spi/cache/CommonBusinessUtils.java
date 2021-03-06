package com.github.flycat.spi.cache;

import com.github.flycat.util.StringUtils;
import com.github.flycat.util.ValueUtils;
import com.github.flycat.util.page.Page;
import com.github.flycat.util.reflect.MethodUtils;
import com.github.flycat.util.reflect.ParameterizedTypeImpl;
import com.google.common.base.CaseFormat;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonBusinessUtils {


    public static <T> Page<T> queryListPage(
            CacheOperation cacheOperation,
            Class<T> clazz,
            Callable<Page<T>> listQuery,
            Function<String, List<Map<String, Integer>>> countQuery,
            Integer pageNum,
            String... countColumns
    ) {
        pageNum = ValueUtils.defaultIfNull(pageNum, 1);

        ParameterizedTypeImpl type = new ParameterizedTypeImpl(new Type[]{clazz}, null, Page.class);
        final Page<T> page = cacheOperation.queryCacheObject(
                new EntityCache(clazz).listPageKey(),
                pageNum,
                type,
                listQuery
        );

        String primaryKey = "id";
        if (countColumns != null && countColumns.length > 0) {
            Method primaryKeyMethod;
            try {
                primaryKeyMethod = clazz.getMethod("get" + StringUtils.capitalize(primaryKey));

                final CountMaps maps = cacheOperation
                        .getCountMapsByModules(
                                page.getList(),
                                (Function<T, Object>) t -> MethodUtils.invoke(primaryKeyMethod, t),
                                (queryKey) -> queryKey.toMaps(countQuery.apply(queryKey.getKeysString()), primaryKey),
                                countColumns
                        );


                List<Method> methods = Arrays.stream(countColumns).map(countColumn ->
                        MethodUtils.getMethod(clazz,
                                "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, countColumn.split("#")[1]),
                                Integer.class))
                        .collect(Collectors.toList());

                List<? extends T> list = page.getList();
                for (T obj : list) {
                    for (int i = 0; i < countColumns.length; i++) {
                        Method method = methods.get(i);
                        method.invoke(obj, maps.getInteger(countColumns[i], primaryKeyMethod.invoke(obj)));
                    }
                }
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
        return page;
    }
}
