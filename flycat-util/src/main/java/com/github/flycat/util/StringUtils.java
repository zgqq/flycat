package com.github.flycat.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringJoiner;

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

    public static boolean hasText(String text) {
        return isNotBlank(text);
    }

    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    public static String joinStringList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        final StringJoiner stringJoiner = new StringJoiner("");
        for (String value : list) {
            stringJoiner.add(value);
        }
        return stringJoiner.toString();
    }

    public static String substring(String str, int length) {
        if (str == null) {
            return null;
        }
        return str.substring(0, Math.min(str.length(), length));
    }

    public static String substringNotInHtmlTag(String str, int length) {
        return substringNotInHtmlTag(substring(str, length));
    }

    public static String substringNotInHtmlTag(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        boolean mayTag = false;
        boolean slash = false;
        int alpNum = 0;

        boolean endTag = false;
        boolean startTag = false;
        boolean mayStartTag = false;

        int startTagIndex = 0;
        for (int length = str.length(); length > 0; length--) {
            final char c = str.charAt(length - 1);
            if (c == '>') {
                mayTag = true;
                alpNum = 0;
                slash = false;
            } else if (CharUtils.isAsciiAlphanumeric(c)) {
                alpNum++;
                slash = false;
            } else if (c == '<') {
                if (alpNum > 0) {
                    if (mayTag) {
                        if (slash) {
                            endTag = true;
                        } else {
                            startTag = true;
                        }
                    } else {
                        mayStartTag = true;
                    }
                    startTagIndex = length - 1;
                    break;
                } else {
                    mayTag = false;
                    alpNum = 0;
                    slash = false;
                }
            } else if (c == '/') {
                slash = true;
            } else {
                mayTag = false;
                alpNum = 0;
                slash = false;
            }
        }

        if (mayStartTag || startTag) {
            return substring(str, startTagIndex);
        }
        return str;
    }

    public static String encodeURLExceptSlash(String url) {
        return encodeURL(url).replace("%2F", "/");
    }

    public static String encodeURL(String url) {
        final String encode;
        try {
            encode = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return encode;
    }
}
