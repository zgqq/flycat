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

import com.google.common.base.Splitter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    public static String decodeOrReturnURL(String url) {
        final String decode;
        try {
            decode = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
        return decode;
    }


    public static String encodeURLExceptPathChars(String url) {
        return encodeURL(url)
                .replace("%2F", "/")
                .replace("%23", "#");
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


    /**
     * 翻译类名称<br/>
     * 将 java/lang/String 的名称翻译成 java.lang.String
     *
     * @param className 类名称 java/lang/String
     * @return 翻译后名称 java.lang.String
     */
    public static String normalizeClassName(String className) {
        return StringUtils.replace(className, "/", ".");
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (hasLength(inString) && hasLength(oldPattern) && newPattern != null) {
            StringBuilder sb = new StringBuilder();
            int pos = 0;
            int index = inString.indexOf(oldPattern);

            for (int patLen = oldPattern.length(); index >= 0; index = inString.indexOf(oldPattern, pos)) {
                sb.append(inString.substring(pos, index));
                sb.append(newPattern);
                pos = index + patLen;
            }

            sb.append(inString.substring(pos));
            return sb.toString();
        } else {
            return inString;
        }
    }

    public static String trimFirstElement(String text, String delimiter) {
        List<String> strings = Splitter
                .on(delimiter).omitEmptyStrings().splitToList(text);
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        ArrayList<String> contents = new ArrayList<>();
        if (!strings.isEmpty()) {
            for (int i = 1; i < strings.size(); i++) {
                contents.add(strings.get(i));
            }
            contents.stream().forEach(str -> stringJoiner.add(str));
        }
        return stringJoiner.toString();
    }

    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }
}
