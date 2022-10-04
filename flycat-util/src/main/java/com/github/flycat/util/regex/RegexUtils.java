package com.github.flycat.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static Matcher matcher( String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher;
    }


    public static String group(String value, String regex, int group) {
        Matcher matcher = matcher(value, regex);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }
}
