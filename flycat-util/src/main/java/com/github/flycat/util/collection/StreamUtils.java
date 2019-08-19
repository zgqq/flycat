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
