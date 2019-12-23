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
package com.github.flycat.util;

import org.apache.commons.collections.IteratorUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    public static Enumeration asEnumeration(Iterator iterator) {
        return IteratorUtils.asEnumeration(iterator);
    }

    public static <T> boolean greater(Collection<T>
                                              collection, int size
    ) {
        if (collection == null) {
            return false;
        }
        return collection.size() > size;
    }

    public static <T> boolean isNotEmpty(Collection<T>
                                                 collection) {
        return collection != null && !collection.isEmpty();
    }


    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public static <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static String toPrettyString(Map<String, String> map) {
        return Arrays.toString(map.entrySet().toArray());
    }
}
