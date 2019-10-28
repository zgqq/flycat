package com.github.flycat.util.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtils {

    public static <T, R> List<R> mapList(List<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T> Map<T, T> toMap(List<Map<String, T>> listMap, String key, String value) {
        return listMap.stream().collect(Collectors.toMap(
                (map -> (T) map.get(key)),
                (map -> (T) map.get(value))));
    }

    public static <T> Map<String, Map<T, T>> toMaps(List<Map<String, T>> listMap, String key,
                                                    String... values) {
        final HashMap<String, Map<T, T>> maps = new HashMap<>();
        for (String value : values) {
            final Map<T, T> map = StreamUtils.toMap(listMap, key,
                    value);
            maps.put(value, map);
        }
        return maps;
    }

}
