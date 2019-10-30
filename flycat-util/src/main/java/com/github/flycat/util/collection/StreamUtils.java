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

import org.apache.commons.lang3.math.NumberUtils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtils {

    public static <T, R> List<R> mapList(List<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <K, T> Map<K, T> toMap(List<Map<String, T>> listMap,
                                         String key,
                                         String value,
                                         Class<K> keyType) {
        PropertyEditor editor = null;
        if (keyType != null) {
            editor = PropertyEditorManager.findEditor(keyType);
        }
        PropertyEditor finalEditor = editor;
        return listMap.stream().collect(Collectors.toMap(
                (map -> {
                    Object t = map.get(key);
                    if (finalEditor != null) {
                        finalEditor.setAsText(t.toString());
                        t = finalEditor.getValue();
                    }
                    return (K) t;
                }),
                (map -> (T) map.get(value))));
    }

    public static <T> Map<String, Map<String, T>> toMaps(List<Map<String, T>> listMap, String key,
                                                         String... values) {
        final HashMap<String, Map<String, T>> maps = new HashMap<>();
        for (String value : values) {
            final Map<String, T> map = StreamUtils.toMap(listMap, key,
                    value, String.class);
            maps.put(value, map);
        }
        return maps;
    }

}
