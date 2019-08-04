package com.github.bootbox.util;


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
