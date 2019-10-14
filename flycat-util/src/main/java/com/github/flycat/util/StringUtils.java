package com.github.flycat.util;

import org.apache.commons.text.StringEscapeUtils;

public class StringUtils {

    public static String capitalize(String str) {
        return org.apache.commons.lang3.StringUtils.capitalize(str);
    }

    public static boolean isBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(str);
    }

    public static String unescapeJson(String text) {
        return StringEscapeUtils.unescapeJson(text);
    }

    public static boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }
}
