/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.util;


import java.util.List;
import java.util.Map;

public final class StringReplacer {

    public static String replace(Map<String, List<String>> contentReplaceMap, String content) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(content) && !contentReplaceMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : contentReplaceMap.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                for (String s : value) {
                    content = content.replace(s, key);
                }
            }
        }
        return content;
    }
}
